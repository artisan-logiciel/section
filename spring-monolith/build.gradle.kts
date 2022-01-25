import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/") }
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

//dependencyManagement {
//    imports {
//        mavenBom("org.testcontainers:testcontainers-bom:${properties["testcontainers_version"]}")
//        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${properties["spring_cloud_version"]}")
//    }
//}

@Suppress("GradlePackageUpdate")
dependencies {
    //Kotlin lib: jdk8, reflexion, coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${properties["kotlinx_serialization_json_version"]}")
    // kotlin TDD
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.projectreactor:reactor-test")
    //jackson mapping (json/xml)
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // String manipulation
    implementation("org.apache.commons:commons-lang3:${properties["commons_lang3_version"]}")
    //Http Request Exception Response
    implementation("org.zalando:problem-spring-webflux:${properties["zalando_problem_version"]}")
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
    implementation("io.netty:netty-tcnative-boringssl-static:${properties["boring_ssl_version"]}")
    // spring Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // Mocking
    testImplementation("io.mockk:mockk:${properties["mockk_version"]}")
    testImplementation("com.github.tomakehurst:wiremock-jre8:${properties["wiremock_version"]}")
    // BDD - Cucumber
    testImplementation("io.cucumber:cucumber-java8:${properties["cucumber_java_version"]}")
    testImplementation("io.cucumber:cucumber-java:${properties["cucumber_java_version"]}")
    // testcontainer
//    testImplementation("org.testcontainers:junit-jupiter")
//    testImplementation("org.testcontainers:postgresql")
//    testImplementation("org.testcontainers:r2dbc")
    //testImplementation("com.tngtech.archunit:archunit-junit5-api:${properties["archunit_junit5_version"]}")
    //testRuntimeOnly("com.tngtech.archunit:archunit-junit5-engine:${properties["archunit_junit5_version"]}")
    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-data")
    testImplementation("org.springframework.security:spring-security-test")
    // JWT authentication
    implementation("io.jsonwebtoken:jjwt-impl:${properties["jsonwebtoken_version"]}")
    implementation("io.jsonwebtoken:jjwt-jackson:${properties["jsonwebtoken_version"]}")
//    implementation("org.springframework.cloud:spring-cloud-config-server")
//    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    // to get Constants
    implementation("org.apache.commons:commons-email:${properties["commons_email_version"]}") {
        exclude("junit")
        exclude("org.easymock")
        exclude("org.powermock")
        exclude("org.slf4j ")
        exclude("commons-io")
        exclude("org.subethamail")
        exclude("com.sun.mail")
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    implementation.configure {
        listOf(
            listOf("org.junit.vintage", "junit-vintage-engine"),
            listOf("org.springframework.boot", "spring-boot-starter-tomcat"),
            listOf("org.apache.tomcat")
        ).map {
            if (it.size == 2)
                exclude(group = it.first(), module = it.last())
            else exclude(group = it.first())
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(properties["free_compiler_args_value"] as String)
        jvmTarget = VERSION_1_8.toString()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging { events(FAILED, SKIPPED) }
    reports {
        @Suppress("DEPRECATION")
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

val cucumberRuntime: Configuration by configurations.creating {
    extendsFrom(configurations["testImplementation"])
}


tasks.register<DefaultTask>("cucumber") {
    group = "verification"
    dependsOn("assemble", "compileTestJava")
    doLast {
        javaexec {
            mainClass.set("io.cucumber.core.cli.Main")
            classpath = cucumberRuntime + sourceSets.main.get().output + sourceSets.test.get().output
            // Change glue for your project package where the step definitions are.
            // And where the feature files are.
            args = listOf(
                "--plugin",
                "pretty",
                "--glue",
                "reaktive.features",
                "src/test/resources/features"
            )
            // Configure jacoco agent for the test coverage in the string interpolation.
            jvmArgs = listOf(
                "-javaagent:${
                    zipTree(
                        configurations
                            .jacocoAgent
                            .get()
                            .singleFile
                    ).filter {
                        it.name == "jacocoagent.jar"
                    }.singleFile
                }=destfile=$buildDir/results/jacoco/cucumber.exec,append=false"
            )
        }
    }
}

tasks.jacocoTestReport {
    // Give jacoco the file generated with the cucumber tests for the coverage.
    executionData(
        files(
            "$buildDir/jacoco/test.exec",
            "$buildDir/results/jacoco/cucumber.exec"
        )
    )
    reports {
        xml.required.set(true)
    }
}