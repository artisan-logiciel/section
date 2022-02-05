import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension

plugins {
    kotlin(module = "multiplatform")
    id("org.jetbrains.compose")
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        @Suppress("UNUSED_VARIABLE") val jsMain by getting {
            dependencies {
                implementation(npm(name = "highlight.js", version = "10.7.2"))
                implementation(dependencyNotation = compose.web.core)
                implementation(dependencyNotation = compose.runtime)
            }
        }
    }
}

// a temporary workaround for a bug in jsRun invocation
afterEvaluate {
    rootProject.extensions.configure<NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.9.0"
    }
}
