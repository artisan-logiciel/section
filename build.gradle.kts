buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.android.tools.build:gradle:4.1.3")
    }
}

group = "education.cccp"
version = "1.0"

allprojects {
    repositories {
        mavenCentral()
    }
}