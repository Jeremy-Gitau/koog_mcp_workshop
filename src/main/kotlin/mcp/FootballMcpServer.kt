package mcp

import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.types.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import kotlinx.serialization.json.*
import source.remote.fetchFootballStandings
import source.remote.fetchTodayMatches
import source.remote.fetchTopScorers

fun runFootballMcpServer() {

    val server = Server(
        serverInfo = Implementation(name = "football-mcp-server", version = "1.0.0"),
        options = ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = true)
            )
        )
    )

    val transport = StdioServerTransport(
        System.`in`.asInput(),
        System.out.asSink().buffered(),
    )

    // Tool 1: fetch standings
    server.addTool(
        name = "get_standings",
        description = "Gets the current league standings/table for a football competition.",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("competition") {
                    put("type", "string")
                    put(
                        "description",
                        "Competition code: PL=Premier League, CL=Champions League, PD=La Liga, BL1=Bundesliga, SA=Serie A"
                    )
                }
            },
            required = listOf("competition")
        )
    ) { request ->
        val competition = request.arguments?.get("competition")?.jsonPrimitive?.content
            ?: return@addTool CallToolResult(content = listOf(TextContent(text = "Parameter 'competition' is required")))

        val data = fetchFootballStandings(competition)
        val table = data.standings.firstOrNull()?.table ?: emptyList()
        val formatted = table.take(10).joinToString("\n") { entry ->
            "${entry.position}. ${entry.team.name} — ${entry.points} pts " +
                    "(W${entry.won} D${entry.draw} L${entry.lost}  GD:${entry.goalDifference})"
        }
        CallToolResult(
            content = listOf(
                TextContent(
                    text = "${data.competition.name} Standings:\n$formatted"
                )
            )
        )
    }

    // Tool 2: today's matches
    server.addTool(
        name = "get_todays_matches",
        description = "Gets all matches scheduled or played today for a football competition.",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("competition") {
                    put("type", "string")
                    put("description", "Competition code: PL, CL, PD, BL1, SA, FL1")
                }
            },
            required = listOf("competition")
        )
    ) { request ->
        val competition = request.arguments?.get("competition")?.jsonPrimitive?.content
            ?: return@addTool CallToolResult(content = listOf(TextContent(text = "Parameter 'competition' is required")))

        val data = fetchTodayMatches(competition)
        if (data.matches.isEmpty()) {
            return@addTool CallToolResult(content = listOf(TextContent(text = "No matches today for $competition")))
        }
        val formatted = data.matches.joinToString("\n") { match ->
            val score = if (match.status == "FINISHED")
                "${match.score.fullTime.home} - ${match.score.fullTime.away}"
            else match.status
            "${match.homeTeam.name} vs ${match.awayTeam.name} [$score]"
        }
        CallToolResult(content = listOf(TextContent(text = "Today's matches:\n$formatted")))
    }

    // Tool 3: top scorers
    server.addTool(
        name = "get_top_scorers",
        description = "Gets the top scorers for a football competition this season.",
        inputSchema = ToolSchema(
            properties = buildJsonObject {
                putJsonObject("competition") {
                    put("type", "string")
                    put("description", "Competition code: PL, CL, PD, BL1, SA, FL1")
                }
                putJsonObject("limit") {
                    put("type", "integer")
                    put("description", "Number of top scorers to return (default 5)")
                }
            },
            required = listOf("competition")
        )
    ) { request ->
        val competition = request.arguments?.get("competition")?.jsonPrimitive?.content
            ?: return@addTool CallToolResult(content = listOf(TextContent(text = "Parameter 'competition' is required")))
        val limit = request.arguments?.get("limit")?.jsonPrimitive?.int ?: 5

        val data = fetchTopScorers(competition, limit)
        val formatted = data.scorers.joinToString("\n") { scorer ->
            "${scorer.player.name} (${scorer.team.name}) — ${scorer.goals} goals in ${scorer.playedMatches} games"
        }
        CallToolResult(
            content = listOf(
                TextContent(
                    text = "${data.competition.name} Top Scorers:\n$formatted"
                )
            )
        )
    }

    runBlocking {
        val session = server.createSession(transport)
        val done = Job()
        session.onClose { done.complete() }
        done.join()
    }
}