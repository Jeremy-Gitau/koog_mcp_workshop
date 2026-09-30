val mcpVersion = "0.9.0"
val slf4jVersion = "2.0.17"

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.gradleup.shadow") version "8.3.9"
    application
}

group = "org.mcp_workshop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.mcp_workshop.MainKt")
}

dependencies {
    testImplementation(kotlin("test"))
    //Ktor
    implementation("io.ktor:ktor-client-core:3.4.1")
    implementation("io.ktor:ktor-client-cio:3.4.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.4.1")
    //mcp
    implementation("io.modelcontextprotocol:kotlin-sdk:${mcpVersion}")
    implementation("org.slf4j:slf4j-simple:${slf4jVersion}")
    //koog
    implementation("ai.koog:koog-agents:1.2.0")
    implementation("ai.koog:koog-agents-additions:1.2.0-beta")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "org.mcp_workshop.MainKt"
    }
}