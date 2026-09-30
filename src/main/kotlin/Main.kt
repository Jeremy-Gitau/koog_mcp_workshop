package org.mcp_workshop

import koog.CryptoKoogAgent
import koog.OfflineKoogAgent
import kotlinx.coroutines.runBlocking

//fun main(args: Array<String>) = runBlocking {
//    when (args.firstOrNull()) {
//        // Football
//        "football-server" -> runFootballMcpServer()
//        "football-agent" -> runFootballCryptoAgent(
//            question = "Who are the top 5 scorers in the Premier League this season?",
//            serverMode = "football-server"
//        )
//        // Crypto
//        "crypto-server" -> runCryptoMcpServer()
//        "crypto-agent" -> runFootballCryptoAgent(
//            question = "What are the top trending coins right now and what is Bitcoin worth in KES?",
//            serverMode = "crypto-server"
//        )
//
//        else -> println("Usage: server | client | agent | gemini | football-server | football-agent | crypto-server | crypto-agent")
//    }
//}



//run the koog agents
fun main(): Unit = runBlocking {

    val koogCryptoAgent = CryptoKoogAgent()
    val koogOfflineAgent = OfflineKoogAgent()

    val question = "What are the top trending coins right now and what is the price of bitcoin right now?"

    println("Gemini Agent running...")
    koogCryptoAgent.runCryptoKoogAgent(
        question = question
    )

    println("................................................")
    println("Gemini Agent stopped")
    println("................................................")

    val result = koogOfflineAgent.agent(
        question = question
    )

    println("nemotron-3-super:cloud model response:")
    println(result)

}