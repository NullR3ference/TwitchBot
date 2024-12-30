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
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "org.aytsan_lex.twitchbot.TwitchBotLauncher"
}
