import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.compose.compose

plugins {
    kotlin(module = "multiplatform")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    google()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/")
}

kotlin {
    jvm(name = "desktop") {
        compilations.all { kotlinOptions.jvmTarget = VERSION_1_8.toString() }
    }
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(dependencyNotation = compose.runtime)
                api(dependencyNotation = compose.foundation)
                api(dependencyNotation = compose.material)
            }
        }
        val commonTest by getting {
            dependencies { implementation(kotlin(simpleModuleName = "test")) }
        }
        val desktopMain by getting {
            dependencies {
                api(dependencyNotation = compose.preview)
            }
        }
        val desktopTest by getting
    }
}