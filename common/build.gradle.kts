import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.compose.compose

plugins {
    kotlin(module = "multiplatform")
    id("org.jetbrains.compose").version("1.0.1")
    id("com.android.library")
}

group = "education.cccp"
version = "1.0"

repositories {
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

kotlin {
    android()
    jvm(name = "desktop") {
        compilations.all { kotlinOptions.jvmTarget = "1.8" }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
        val commonTest by getting {
            dependencies { implementation(kotlin(simpleModuleName = "test")) }
        }
        val desktopMain by getting {
            dependencies { api(compose.preview) }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(apiLevel = 31)
    defaultConfig {
        minSdkVersion(minSdkVersion = 24)
        targetSdkVersion(targetSdkVersion = 31)
    }
    compileOptions {
        sourceCompatibility = VERSION_1_8
        targetCompatibility = VERSION_1_8
    }
}