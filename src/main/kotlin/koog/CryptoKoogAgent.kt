package koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.mcp.McpToolRegistryProvider
import ai.koog.agents.mcp.fromProcess
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mcp.McpClient

class CryptoKoogAgent {

    suspend fun runCryptoKoogAgent(question: String) {

        val apiKey = "YOUR_GEMINI_API_KEY"

        // 1. Start the crypto MCP server as a subprocess (same server as the manual demo)
        val client = McpClient()
        val process = withContext(Dispatchers.IO) { client.startServerProcess("crypto-server") }

        try {
            // 2. Discover its tools over stdio and hand them to Koog as a ToolRegistry
            val toolRegistry = McpToolRegistryProvider.fromProcess(process)
            println("Available tools: ${toolRegistry.tools.map { it.name }}\n")

            // 3. Model + tools is the whole agent. Koog owns the tool-calling loop.
            val agent = AIAgent(
                promptExecutor = simpleGoogleAIExecutor(apiKey),
                llmModel = GoogleModels.Gemini2_5Flash,
                toolRegistry = toolRegistry,
                systemPrompt = "You are a helpful assistant with access to live cryptocurrency data."
            )

            println("Question: $question\n")
            val answer = agent.run(question)
            println("Final Answer:\n$answer")
        } finally {
            process.destroy()
        }
    }

}
