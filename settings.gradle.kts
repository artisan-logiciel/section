pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://repo.spring.io/milestone")
        maven("https://repo.spring.io/snapshot")
    }
    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        kotlin("plugin.serialization").version(extra["kotlin.version"] as String)
        kotlin("plugin.allopen").version(extra["kotlin.version"] as String)
        kotlin("plugin.noarg").version(extra["kotlin.version"] as String)
        kotlin("plugin.spring").version(extra["kotlin.version"] as String)
        id("io.spring.dependency-management").version(extra["spring_dependency_management.version"] as String)
        id("com.google.cloud.tools.jib").version(extra["jib.version"] as String)
        id("org.springframework.boot").version(extra["springboot.version"] as String)
    }
//    buildscript {
//        repositories {
//            gradlePluginPortal()
//            maven("https://repo.spring.io/milestone")
//            maven("https://repo.spring.io/snapshot")
//            mavenCentral()
//        }
//    }
}
rootProject.name = "section"


include(":android")
include(":desktop")
include(":common")
include(":browser")
include(":simple-server")
include(":server")

