package koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.agents.mcp.fromProcess
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.executor.ollama.client.OllamaModels
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.rag.base.files.JVMFileSystemProvider
import ai.koog.skills.discovery.discoverSkills
import ai.koog.skills.prompt.SkillsPromptFormat
import ai.koog.skills.prompt.generateSkillsPrompt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mcp.McpClient
import java.io.File

class OfflineKoogAgent {

    @Tool
    @LLMDescription("Ask the user a question by sending it to stdout and return the answer from stdin")
    fun askUser(
        @LLMDescription("Question from the agent")
        question: String
    ): String {
        println(question)
        return readln()
    }

    val skillsRoot: String = File("src/main/kotlin/koog/skills").absolutePath

    suspend fun agent(question: String): String {

        val discoveredSkills = discoverSkills(JVMFileSystemProvider.ReadOnly, listOf(skillsRoot))

        val skillsPrompt = generateSkillsPrompt(discoveredSkills, SkillsPromptFormat.XML)

        println("Discovered skills: ${discoveredSkills.map { it.name }}\n")
        println("Processing skills...")
        println("skillsPrompt: $skillsPrompt\n")

        val nemotron = LLModel(
            provider = LLMProvider.Ollama,
            id = "nemotron-3-super:cloud",
            capabilities = listOf(
                LLMCapability.Temperature,
            ),
            contextLength = 128_000
        )

        OllamaModels.addCustomModel(nemotron)

        val mcpClient = McpClient()
        val server = withContext(Dispatchers.IO) {
            mcpClient.startServerProcess("crypto-server")
        }

        val toolRegistry = McpToolRegistryProvider.fromProcess(server)
        println("available tools: ${toolRegistry.tools.map { it.name }}")


        val agent = AIAgent(
            promptExecutor = MultiLLMPromptExecutor(OllamaClient()),
            llmModel = nemotron,
            toolRegistry = ToolRegistry {
                tools(toolRegistry.tools)
//                tool(::askUser)
            },
            systemPrompt = """
                You are a helpful assistant with access to live cryptocurrency data."
                Use the available skills listed below.
                Before using a skill script, disclose the skills by listing and reading files with tools.
                $skillsPrompt
            """.trimIndent(),
        ) {
            handleEvents {
                onToolCallStarting { eventContext ->
                    println("Tool called: ${eventContext.toolName} with args ${eventContext.toolArgs}")
                }
            }
        }

        val result = agent.run(
            question
        )
        return result
    }
}