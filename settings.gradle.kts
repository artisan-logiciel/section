pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://repo.spring.io/milestone")
        maven(url = "https://repo.spring.io/snapshot")
    }
    plugins {
        kotlin(module = "jvm").version(extra["kotlin.version"].toString())
        kotlin(module = "plugin.serialization").version(extra["kotlin.version"].toString())
        kotlin(module = "plugin.allopen").version(extra["kotlin.version"].toString())
        kotlin(module = "plugin.noarg").version(extra["kotlin.version"].toString())
        kotlin(module = "plugin.spring").version(extra["kotlin.version"].toString())
        id("io.spring.dependency-management")
            .version(extra["spring_dependency_management.version"].toString())
        id("com.google.cloud.tools.jib").version(extra["jib.version"].toString())
        id("org.springframework.boot").version(extra["springboot.version"].toString())
        kotlin("multiplatform").version(extra["kotlin.version"].toString())
        id("org.jetbrains.compose").version(extra["compose.version"].toString())
    }
}

rootProject.name = "section"
include(":common")
include(":desktop")
include(":server")
include(":monolith")
include(":browser")