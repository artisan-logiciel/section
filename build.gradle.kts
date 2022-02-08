buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(dependencyNotation = "org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["kotlin.version"]}")
        classpath(dependencyNotation = "com.android.tools.build:gradle:${properties["android_gradle.version"]}")
    }
}

group = properties["artifact.group"].toString()
version = properties["artifact.version"].toString()

allprojects { repositories { mavenCentral() } }