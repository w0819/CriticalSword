import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.22")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.22")

    implementation("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    implementation("io.github.monun:kommand-api:2.14.0")
    implementation("io.github.monun:tap-api:4.7.3")
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