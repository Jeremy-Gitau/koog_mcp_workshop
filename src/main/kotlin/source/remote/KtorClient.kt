package source.remote

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val json = Json { ignoreUnknownKeys = true }

fun httpClient() = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(json)
    }
}