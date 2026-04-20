package agent

import agent.models.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequestParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import mcp.McpClient
import source.remote.httpClient
import source.remote.json


suspend fun runFootballCryptoAgent(question: String, serverMode: String) {

    val apiKey = "YOUR_GEMINI_API_KEY"
    val model = "gemini-2.5-flash"
    val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
    val client = McpClient()

    // 1. Start the MCP server
    val process = withContext(Dispatchers.IO) { client.startServerProcess(serverMode) }
    val mcpClient = client.connectMcpClient(process)
    println("Connected to MCP server ($serverMode)!")

    // 2. Discover tools dynamically from the server
    val mcpTools = mcpClient.listTools().tools
    println("Available tools: ${mcpTools.map { it.name }}\n")

    val geminiTools = listOf(
        GeminiTool(
            functionDeclarations = mcpTools.map { tool ->
                GeminiFunctionDeclaration(
                    name = tool.name,
                    description = tool.description ?: "",
                    parameters = buildJsonObject {
                        put("type", "object")
                        val props = tool.inputSchema.properties
                        if (props != null) put("properties", props)
                        val required = tool.inputSchema.required
                        if (!required.isNullOrEmpty()) {
                            put("required", buildJsonArray { required.forEach { add(it) } })
                        }
                    }
                )
            }
        )
    )

    // 3. Start conversation
    val contents = mutableListOf(
        GeminiContent(role = "user", parts = listOf(GeminiPart(text = question)))
    )

    println("Question: $question\n")

    // 4. Agentic loop — Gemini may call multiple tools
    var keepLooping = true
    while (keepLooping) {
        val response = httpClient().post(baseUrl) {
            contentType(ContentType.Application.Json)
            setBody(GeminiRequest(contents = contents, tools = geminiTools))
        }
        val geminiResponse = json.decodeFromString<GeminiResponse>(response.bodyAsText())
        val parts = geminiResponse.candidates.first().content.parts

        val functionCall = parts.firstOrNull { it.functionCall != null }?.functionCall

        if (functionCall != null) {
            println("→ Gemini calling tool: ${functionCall.name} with args: ${functionCall.args}")

            val result = mcpClient.callTool(
                CallToolRequest(
                    params = CallToolRequestParams(
                        name = functionCall.name,
                        arguments = functionCall.args
                    )
                )
            )
            val toolResult = result.content.joinToString { it.toString() }
            println("← Tool result: $toolResult\n")

            // Add model turn + tool result back to history
            contents.add(GeminiContent(role = "model", parts = listOf(GeminiPart(functionCall = functionCall))))
            contents.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(
                        GeminiPart(
                            functionResponse = GeminiFunctionResponse(
                                name = functionCall.name,
                                response = buildJsonObject { put("result", toolResult) }
                            )
                        ))
                ))
        } else {
            // No more tool calls — final answer
            println("✅ Final Answer:\n${parts.firstOrNull()?.text}")
            keepLooping = false
        }
    }

    httpClient().close()
    mcpClient.close()
    process.destroy()
}