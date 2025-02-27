plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation("com.github.twitch4j:twitch4j:1.24.0")
    implementation("com.github.philippheuer.events4j:events4j-handler-simple:0.12.2")
    implementation("com.github.philippheuer.events4j:events4j-handler-reactor:0.12.2")
    implementation("ch.qos.logback", "logback-classic","1.3.5")
    implementation("io.github.ollama4j:ollama4j:1.0.95")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.java-websocket:Java-WebSocket:1.6.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass = "org.aytsan_lex.twitchbot.TwitchBotLauncher"
}
