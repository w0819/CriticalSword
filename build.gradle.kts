import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation(dependencyNotation = "io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    implementation("io.github.monun:kommand-api:3.1.2")
    implementation("io.github.monun:tap-api:4.9.2")
    implementation("io.github.monun:heartbeat-coroutines:0.0.5")
    implementation("io.github.monun:invfx-api:3.3.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("CriticalSwordKt")
}