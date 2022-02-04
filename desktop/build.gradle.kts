import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*

plugins {
    kotlin(module = "multiplatform")
    id( "org.jetbrains.compose").version("1.0.1")
}

repositories {
    google()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all { kotlinOptions.jvmTarget = "1.8" }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(path = ":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(Dmg, Msi, Deb)
            packageName = "jvm"
            packageVersion = "1.0.0"
        }
    }
}