@file:Suppress(
    "GradlePackageUpdate",
    "DEPRECATION",
)

import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import java.io.ByteArrayOutputStream

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
    jacoco
}

//allOpen{
//    annotation("javax.validation.constraints.NotBlank")
//    annotation("javax.validation.constraints.Pattern")
//    annotation("javax.validation.constraints.Size")
//    annotation("javax.validation.constraints.Email")
//    annotation("javax.validation.constraints.NotNull")
//}
repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/")
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
}

//dependencyManagement {
//    imports {
//        mavenBom("org.testcontainers:testcontainers-bom:${properties["testcontainers.version"]}")
//        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${properties["spring_cloud.version"]}")
//    }
//}

dependencies {
//    implementation(project(path = ":common"))
    //Kotlin lib: jdk8, reflexion, coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
//    implementation("io.projectreactor.tools:blockhound:${properties["blockhound_version"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${properties["kotlinx_serialization_json.version"]}")
    // kotlin TDD
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.projectreactor:reactor-test")
//    testImplementation("io.projectreactor.tools:blockhound-junit-platform:${properties["blockhound_version"]}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${properties["mockito_kotlin_version"]}")

    //jackson mapping (json/xml)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // String manipulation
    implementation("org.apache.commons:commons-lang3")
    //Http Request Exception Response
    implementation("org.zalando:problem-spring-webflux:${properties["zalando_problem.version"]}")
    //spring conf
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    //spring dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    //spring actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    //spring r2dbc
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    //spring javax.mail
    implementation("org.springframework.boot:spring-boot-starter-mail")
    //Spring bean validation JSR 303
    implementation("org.springframework.boot:spring-boot-starter-validation")
    //spring thymeleaf for mail templating
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    //spring webflux reactive http
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    //H2database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")
    //Postgresql
//    runtimeOnly("io.r2dbc:r2dbc-postgresql")
//    runtimeOnly("org.postgresql:postgresql")
    //SSL
    implementation("io.netty:netty-tcnative-boringssl-static:${properties["boring_ssl.version"]}")
    // spring Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") { exclude(module = "mockito-core") }
    // Mocking
    testImplementation("io.mockk:mockk:${properties["mockk.version"]}")
    testImplementation("com.github.tomakehurst:wiremock-jre8:${properties["wiremock.version"]}")
    testImplementation("com.ninja-squad:springmockk:3.1.0")


    // testcontainer
//    testImplementation("org.testcontainers:junit-jupiter")
//    testImplementation("org.testcontainers:postgresql")
//    testImplementation("org.testcontainers:r2dbc")
    //testImplementation("com.tngtech.archunit:archunit-junit5-api:${properties["archunit_junit5_version"]}")
    //testRuntimeOnly("com.tngtech.archunit:archunit-junit5-engine:${properties["archunit_junit5_version"]}")
    // Spring Security
//    implementation( "org.springframework.boot:spring-boot-starter-security")
//    implementation( "org.springframework.security:spring-security-data")
//    testImplementation( "org.springframework.security:spring-security-test")
    // JWT authentication
    implementation("io.jsonwebtoken:jjwt-impl:${properties["jsonwebtoken.version"]}")
    implementation("io.jsonwebtoken:jjwt-jackson:${properties["jsonwebtoken.version"]}")
//    testImplementation( "org.springframework.cloud:spring-cloud-starter-contract-verifier")
    // to get Constants
    implementation("org.apache.commons:commons-email:${properties["commons_email.version"]}") {
        exclude(group = "junit")
        exclude(group = "org.easymock")
        exclude(group = "org.powermock")
        exclude(group = "org.slf4j ")
        exclude(group = "commons-io")
        exclude(group = "org.subethamail")
        exclude(group = "com.sun.mail")
    }
}

configurations {
    compileOnly { extendsFrom(configurations.annotationProcessor.get()) }
    implementation.configure {
        setOf(
            setOf("org.junit.vintage", "junit-vintage-engine"),
            setOf("org.springframework.boot", "spring-boot-starter-tomcat"),
            setOf("org.apache.tomcat")
        ).map {
            when (it.size) {
                2 -> exclude(group = it.first(), module = it.last())
                else -> exclude(group = it.first())
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(properties["free_compiler_args_value"].toString())
        jvmTarget = VERSION_1_8.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging { events(FAILED, SKIPPED) }
    reports {
        html.isEnabled = true
        ignoreFailures = true
    }
}


tasks.register<Delete>("cleanResources") {
    description = "Delete directory build/resources"
    group = "build"
    delete("build/resources")
}



tasks.register<TestReport>("testReport") {
    description = "Generates an HTML test report from the results of testReport task."
    group = "report"
    destinationDir = file("$buildDir/reports/tests")
    reportOn("test")
}

open class DeployGAE : Exec() {
    init {
        this.workingDir = project.rootDir
        this.commandLine(
            "/snap/bin/gcloud",
            "-v"
//            "app",
//            "deploy",
//            "${projectDir.absolutePath}/src/main/appengine/app.yml"
        )
        this.standardOutput = ByteArrayOutputStream()
    }
}


tasks.register<DeployGAE>("deployGAE") {
    group = "application"
    val cmd = "gcloud app deploy src/main/appengine/app.flexible.yml"
    doLast { println(cmd) }
}

