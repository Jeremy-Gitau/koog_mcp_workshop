package agent

import mcp.McpClient


suspend fun runFootballCryptoAgent(question: String, serverMode: String) {

    val apiKey = "apikey"
    val model = "gemini-2.5-flash"
    val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
    val client = McpClient()

    // 1. Start the correct MCP server


    // 2. Discover tools dynamically from the server


    // 3. Start conversation


    // 4. Agentic loop — Gemini may call multiple tools

}