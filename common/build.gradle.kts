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
    js(LEGACY) {
        binaries.executable()
        browser {
            commonWebpackConfig { cssSupport.enabled = true }
        }
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
        val androidMain by getting {
            dependencies {
                api(dependencyNotation = "androidx.appcompat:appcompat:1.4.1")
                api(dependencyNotation = "androidx.core:core-ktx:1.7.0")
            }
        }
        val androidTest by getting {
            dependencies { implementation(dependencyNotation = "junit:junit:4.13.2") }
        }
        val desktopMain by getting {
            dependencies { api(compose.preview) }
        }
        val desktopTest by getting
        val jsMain by getting {
            dependencies { implementation(dependencyNotation = "org.jetbrains.kotlinx:kotlinx-html:0.7.3") }
        }
        val jsTest by getting
    }
}

android {
    compileSdkVersion(apiLevel = 31)
    sourceSets["main"].manifest.srcFile(srcPath = "src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(31)
    }
    compileOptions {
        sourceCompatibility = VERSION_1_8
        targetCompatibility = VERSION_1_8
    }
}