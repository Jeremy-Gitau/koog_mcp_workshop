package mcp

import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.StdioClientTransport
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered

private const val MCP_SERVER_JAR_PATH = "build/libs/mcp_workshop-1.0-SNAPSHOT-all.jar"

class McpClient {

    fun startServerProcess(mode: String): Process {
        return ProcessBuilder(
            "java", "-jar",
            MCP_SERVER_JAR_PATH,
            mode
        ).redirectErrorStream(false).start()
    }

    suspend fun connectMcpClient(process: Process): Client {
        val client = Client(clientInfo = Implementation(name = "workshop-agent", version = "1.0.0"))
        val transport = StdioClientTransport(
            input = process.inputStream.asSource().buffered(),
            output = process.outputStream.asSink().buffered(),
        )
        client.connect(transport)
        return client
    }
}
