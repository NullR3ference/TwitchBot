plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.guava)
    implementation("com.github.twitch4j:twitch4j:1.23.0")
    implementation("com.github.philippheuer.events4j:events4j-handler-simple:0.12.2")
    implementation("ch.qos.logback", "logback-classic","1.3.5")
    implementation("io.github.ollama4j:ollama4j:1.0.79")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "org.aytsan_lex.twitchbot.TwitchBotLauncher"
}
