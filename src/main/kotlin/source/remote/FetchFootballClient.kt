package source.remote

private const val FOOTBALL_API_KEY = "api key"
private const val BASE_URL = "https://api.football-data.org/v4"

// Competition codes: PL = Premier League, CL = Champions League,
// PD = La Liga, BL1 = Bundesliga, SA = Serie A, FL1 = Ligue 1

//suspend fun fetchFootballStandings(competitionCode: String): FootballStandingsResponse {
//    val client = httpClient()
//    val response = client.get("$BASE_URL/competitions/$competitionCode/standings") {
//        header("X-Auth-Token", FOOTBALL_API_KEY)
//    }
//    client.close()
//    return json.decodeFromString(response.bodyAsText())
//}
//
//suspend fun fetchTodayMatches(competitionCode: String): FootballMatchesResponse {
//    val client = httpClient()
//    val today = LocalDate.now().toString()
//    val response = client.get("$BASE_URL/competitions/$competitionCode/matches") {
//        header("X-Auth-Token", FOOTBALL_API_KEY)
//        parameter("dateFrom", today)
//        parameter("dateTo", today)
//    }
//    client.close()
//    return json.decodeFromString(response.bodyAsText())
//}
//
//suspend fun fetchTopScorers(competitionCode: String, limit: Int = 10): FootballScorersResponse {
//    val client = httpClient()
//    val response = client.get("$BASE_URL/competitions/$competitionCode/scorers") {
//        header("X-Auth-Token", FOOTBALL_API_KEY)
//        parameter("limit", limit)
//    }
//    client.close()
//    return json.decodeFromString(response.bodyAsText())
//}