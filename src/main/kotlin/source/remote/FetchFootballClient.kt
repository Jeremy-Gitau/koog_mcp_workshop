package source.remote

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import org.mcp_workshop.source.remote.models.FootballMatchesResponse
import org.mcp_workshop.source.remote.models.FootballScorersResponse
import org.mcp_workshop.source.remote.models.FootballStandingsResponse
import java.time.LocalDate

private const val FOOTBALL_API_KEY = "YOUR_FOOTBALL_API_KEY"
private const val BASE_URL = "https://api.football-data.org/v4"

// Competition codes: PL = Premier League, CL = Champions League,
// PD = La Liga, BL1 = Bundesliga, SA = Serie A, FL1 = Ligue 1

suspend fun fetchFootballStandings(competitionCode: String): FootballStandingsResponse {
    val client = httpClient()
    val response = client.get("$BASE_URL/competitions/$competitionCode/standings") {
        header("X-Auth-Token", FOOTBALL_API_KEY)
    }
    client.close()
    return json.decodeFromString(response.bodyAsText())
}

suspend fun fetchTodayMatches(competitionCode: String): FootballMatchesResponse {
    val client = httpClient()
    val today = LocalDate.now().toString()
    val response = client.get("$BASE_URL/competitions/$competitionCode/matches") {
        header("X-Auth-Token", FOOTBALL_API_KEY)
        parameter("dateFrom", today)
        parameter("dateTo", today)
    }
    client.close()
    return json.decodeFromString(response.bodyAsText())
}

suspend fun fetchTopScorers(competitionCode: String, limit: Int = 10): FootballScorersResponse {
    val client = httpClient()
    val response = client.get("$BASE_URL/competitions/$competitionCode/scorers") {
        header("X-Auth-Token", FOOTBALL_API_KEY)
        parameter("limit", limit)
    }
    client.close()
    return json.decodeFromString(response.bodyAsText())
}