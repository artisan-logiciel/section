@file:Suppress("suppressKotlinVersionCompatibilityCheck")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin(module = "jvm")
    application
}

group = "education.cccp"
version = "1.0"

repositories {
    mavenCentral()
    google()
    maven(url="https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    implementation(project(path = ":common"))
    testImplementation(kotlin(module = "test"))
    implementation(dependencyNotation = "io.ktor:ktor-server-netty:1.6.7")
    implementation(dependencyNotation = "io.ktor:ktor-html-builder:1.6.7")
    implementation(dependencyNotation = "org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
}

tasks.test { useJUnitPlatform() }

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "17" }

application { mainClass.set("education.cccp.server.SimpleServerKt") }