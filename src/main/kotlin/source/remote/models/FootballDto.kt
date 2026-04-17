package org.mcp_workshop.source.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class FootballStandingsResponse(
    val competition: FootballCompetition,
    val standings: List<FootballStandingTable>
)

@Serializable
data class FootballCompetition(
    val name: String
)

@Serializable
data class FootballStandingTable(
    val type: String,
    val table: List<FootballTableEntry>
)

@Serializable
data class FootballTableEntry(
    val position: Int,
    val team: FootballTeam,
    val playedGames: Int,
    val won: Int,
    val draw: Int,
    val lost: Int,
    val points: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int
)

@Serializable
data class FootballTeam(
    val name: String
)

// ---- Matches ----

@Serializable
data class FootballMatchesResponse(
    val matches: List<FootballMatch>
)

@Serializable
data class FootballMatch(
    val utcDate: String,
    val status: String,
    val homeTeam: FootballTeam,
    val awayTeam: FootballTeam,
    val score: FootballScore
)

@Serializable
data class FootballScore(
    val fullTime: FootballScoreDetail,
    val halfTime: FootballScoreDetail
)

@Serializable
data class FootballScoreDetail(
    val home: Int? = null,
    val away: Int? = null
)

// ---- Top Scorers ----

@Serializable
data class FootballScorersResponse(
    val competition: FootballCompetition,
    val scorers: List<FootballScorer>
)

@Serializable
data class FootballScorer(
    val player: FootballPlayer,
    val team: FootballTeam,
    val goals: Int,
    val assists: Int? = null,
    val playedMatches: Int
)

@Serializable
data class FootballPlayer(
    val name: String,
    val nationality: String
)