# MCP Workshop

A small Kotlin/JVM workshop project for learning how to build:

- MCP servers over stdio
- an MCP client that launches and connects to a local server process
- an agent loop that lets Gemini discover MCP tools and call them dynamically
- simple data integrations against football and cryptocurrency APIs

The repository is structured as an educational sample rather than a polished production app. It already builds successfully, but parts of the runtime flow still depend on local setup and hardcoded configuration.

## What This Project Does

The project has two main domains:

- Football: exposes standings, today's matches, and top scorers as MCP tools
- Crypto: exposes coin price lookup, trending coins, and market overview as MCP tools

On top of that, there is an agent example that:

1. starts one of the MCP servers as a subprocess
2. connects to it through the Kotlin MCP SDK
3. lists the available tools from the server
4. passes those tools to Gemini as function declarations
5. lets Gemini choose and call tools until it reaches a final answer

## Tech Stack

- Kotlin JVM
- Gradle
- Ktor client
- Kotlinx Serialization
- Model Context Protocol Kotlin SDK
- Shadow Jar plugin for a fat runnable jar

## Project Layout

```text
src/main/kotlin/
  Main.kt                         CLI entrypoint for server and agent modes
  agent/
    FootballCryptoAgent.kt        Gemini + MCP agent loop
    models/GeminiDomain.kt        Gemini request/response DTOs
  mcp/
    FootballMcpServer.kt          Football MCP server and tools
    CryptoMcpServer.kt            Crypto MCP server and tools
    McpClient.kt                  Launches and connects to MCP server processes
  source/remote/
    KtorClient.kt                 Shared HTTP client + JSON config
    FetchFootballClient.kt        football-data.org integration
    FetchCryptoClient.kt          CoinGecko integration
    models/
      FootballDto.kt              Football API DTOs
      CryptoDto.kt                Crypto API DTOs
      FetchWeatherDto.kt          Unused weather DTOs
```

## How The Pieces Fit Together

### MCP servers

Each server uses `Server` + `StdioServerTransport` from the MCP Kotlin SDK and registers tools with JSON schemas.

Football tools:

- `get_standings`
- `get_todays_matches`
- `get_top_scorers`

Crypto tools:

- `get_coin_price`
- `get_trending_coins`
- `get_market_overview`

### Remote data clients

The servers call external APIs through Ktor:

- football data comes from `https://api.football-data.org/v4`
- crypto data comes from `https://api.coingecko.com/api/v3`

### Agent flow

`agent/FootballCryptoAgent.kt` starts a server process, connects with `McpClient`, converts MCP tool schemas into Gemini function declarations, and runs a tool-calling loop until Gemini returns a final text answer.

## Prerequisites

- JDK 17
- Internet access for API calls
- A `football-data.org` API key
- A Gemini API key

CoinGecko endpoints used here do not currently require an API key in this codebase.

## Build

From the project root:

```bash
./gradlew build
```

This was verified successfully in the current repository state.

To build the fat jar:

```bash
./gradlew shadowJar
```

Generated jars:

- `build/libs/mcp_workshop-1.0-SNAPSHOT.jar`
- `build/libs/mcp_workshop-1.0-SNAPSHOT-all.jar`

## Setup

### 1. Add your API keys

Replace the placeholder constants in these files:

- `src/main/kotlin/source/remote/FetchFootballClient.kt`
  - `private const val FOOTBALL_API_KEY = "YOUR_FOOTBALL_API_KEY"`
- `src/main/kotlin/agent/FootballCryptoAgent.kt`
  - `val apiKey = "YOUR_GEMINI_API_KEY"`

For a real project, these should be moved to environment variables or Gradle properties instead of being hardcoded in source.

### 2. Set the MCP server jar path

`src/main/kotlin/mcp/McpClient.kt` currently uses:

```kotlin
private const val MCP_SERVER_JAR_PATH = "build/libs/mcp_workshop-1.0-SNAPSHOT-all.jar"
```

This works when you run from the repository root after building the fat jar. If you want the agent mode to work from arbitrary directories or packaged distributions, move this value to an environment variable or system property.

## Intended Run Modes

Once the API keys are set and the fat jar has been built, the intended workflow is:

### Run the football MCP server

```bash
java -jar build/libs/mcp_workshop-1.0-SNAPSHOT-all.jar football-server
```

### Run the crypto MCP server

```bash
java -jar build/libs/mcp_workshop-1.0-SNAPSHOT-all.jar crypto-server
```

### Run the football agent example

```bash
java -jar build/libs/mcp_workshop-1.0-SNAPSHOT-all.jar football-agent
```

### Run the crypto agent example

```bash
java -jar build/libs/mcp_workshop-1.0-SNAPSHOT-all.jar crypto-agent
```

## Example Questions The Agent Is Designed To Handle

- "Who are the top 5 scorers in the Premier League this season?"
- "Show me today's Champions League matches."
- "What are the top trending coins right now?"
- "What is Bitcoin worth in KES?"
- "Give me the top 10 coins by market cap in USD."

## Current Limitations

- `McpClient.kt` assumes the process is launched from the repository root so the relative jar path resolves correctly.
- API keys are hardcoded placeholders in source files.
- There are no automated tests yet.
- `FetchWeatherDto.kt` is present but currently unused.
- Package naming is inconsistent across the project (`org.mcp_workshop`, `mcp`, `agent`, `source.remote`), which is workable but confusing in a teaching repo.

## Suggested Next Improvements

- Move secrets to environment variables
- Move the jar path to environment variables or a Gradle/system property
- Re-enable and clean up the CLI entrypoint
- Add tests for remote client parsing and MCP tool handlers
- Standardize package names
- Add error handling for failed external API calls and invalid tool inputs

## Notes For Workshop Use

This repo is a good demonstration of the basic MCP pattern:

1. wrap an external capability behind tools
2. expose those tools through an MCP server
3. connect a client to that server
4. let an LLM discover and use the tools dynamically

If you want to extend the workshop, the easiest next step is to add a new tool to either server and let the agent pick it up automatically through `listTools()`.
