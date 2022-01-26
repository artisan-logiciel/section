pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url ="https://repo.spring.io/milestone")
        maven(url ="https://repo.spring.io/snapshot")
    }
    plugins {
        kotlin(module = "jvm").version(extra["kotlin.version"] as String)
        kotlin(module ="plugin.serialization").version(extra["kotlin.version"] as String)
        kotlin(module ="plugin.allopen").version(extra["kotlin.version"] as String)
        kotlin(module ="plugin.noarg").version(extra["kotlin.version"] as String)
        kotlin(module ="plugin.spring").version(extra["kotlin.version"] as String)
        id("io.spring.dependency-management").version(extra["spring_dependency_management.version"] as String)
        id("com.google.cloud.tools.jib").version(extra["jib.version"] as String)
        id("org.springframework.boot").version(extra["springboot.version"] as String)
    }
}

rootProject.name = "section"
include(":android")
include(":desktop")
include(":common")
include(":browser")
include(":simple-server")
include(":backend")

