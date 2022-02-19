import org.gradle.api.JavaVersion.VERSION_1_8
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(module = "jvm")
    kotlin(module = "plugin.spring")
    kotlin(module = "plugin.allopen")
    kotlin(module = "plugin.noarg")
    kotlin(module = "plugin.serialization")
    id("org.springframework.boot")
    id( "io.spring.dependency-management")
//    id( "com.google.cloud.tools.jib")
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
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/")
    maven(url = "https://repo.spring.io/milestone")
    maven(url = "https://repo.spring.io/snapshot")
}

//dependencyManagement {
//    imports {
//        mavenBom("org.testcontainers:testcontainers-bom:${properties["testcontainers.version"]}")
//        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${properties["spring_cloud.version"]}")
//    }
//}

@Suppress("GradlePackageUpdate")
dependencies {
//    implementation(project(path = ":common"))
    //Kotlin lib: jdk8, reflexion, coroutines
    implementation(dependencyNotation = "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(dependencyNotation = "org.jetbrains.kotlin:kotlin-reflect")
    implementation(dependencyNotation = "io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(dependencyNotation = "org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation(dependencyNotation = "org.jetbrains.kotlinx:kotlinx-serialization-json:${properties["kotlinx_serialization_json.version"]}")
    // kotlin TDD
    testImplementation(dependencyNotation = "org.jetbrains.kotlin:kotlin-test")
    testImplementation(dependencyNotation = "org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(dependencyNotation = "io.projectreactor:reactor-test")
    //jackson mapping (json/xml)
    implementation(dependencyNotation = "com.fasterxml.jackson.module:jackson-module-kotlin")
    // String manipulation
    implementation(dependencyNotation = "org.apache.commons:commons-lang3")
    //Http Request Exception Response
//    implementation(dependencyNotation = "org.zalando:problem-spring-webflux:${properties["zalando_problem.version"]}")
    //spring conf
    annotationProcessor(dependencyNotation = "org.springframework.boot:spring-boot-configuration-processor")
    //spring dev tools
    developmentOnly(dependencyNotation = "org.springframework.boot:spring-boot-devtools")
    //spring actuator
    implementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-actuator")
    //spring r2dbc
    implementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-data-r2dbc")
    //spring javax.mail
    implementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-mail")
    //Spring bean validation JSR 303
    implementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-validation")
    //spring thymeleaf for mail templating
    implementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-thymeleaf")
    //spring webflux reactive http
    implementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-webflux")
    //H2database
    runtimeOnly(dependencyNotation = "com.h2database:h2")
    runtimeOnly(dependencyNotation = "io.r2dbc:r2dbc-h2")
    //Postgresql
//    runtimeOnly("io.r2dbc:r2dbc-postgresql")
//    runtimeOnly("org.postgresql:postgresql")
    //SSL
    implementation(dependencyNotation = "io.netty:netty-tcnative-boringssl-static:${properties["boring_ssl.version"]}")
    // spring Test dependencies
    testImplementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-test") { exclude(module = "mockito-core") }
    // Mocking
    testImplementation(dependencyNotation = "io.mockk:mockk:${properties["mockk.version"]}")
    testImplementation(dependencyNotation = "com.github.tomakehurst:wiremock-jre8:${properties["wiremock.version"]}")
    testImplementation(dependencyNotation = "com.ninja-squad:springmockk:3.1.0")

    // BDD - Cucumber
    testImplementation(dependencyNotation = "io.cucumber:cucumber-java8:${properties["cucumber_java.version"]}")
    testImplementation(dependencyNotation = "io.cucumber:cucumber-java:${properties["cucumber_java.version"]}")
    // testcontainer
//    testImplementation("org.testcontainers:junit-jupiter")
//    testImplementation("org.testcontainers:postgresql")
//    testImplementation("org.testcontainers:r2dbc")
    //testImplementation("com.tngtech.archunit:archunit-junit5-api:${properties["archunit_junit5_version"]}")
    //testRuntimeOnly("com.tngtech.archunit:archunit-junit5-engine:${properties["archunit_junit5_version"]}")
    // Spring Security
//    implementation(dependencyNotation = "org.springframework.boot:spring-boot-starter-security")
//    implementation(dependencyNotation = "org.springframework.security:spring-security-data")
//    testImplementation(dependencyNotation = "org.springframework.security:spring-security-test")
    // JWT authentication
    implementation(dependencyNotation = "io.jsonwebtoken:jjwt-impl:${properties["jsonwebtoken.version"]}")
    implementation(dependencyNotation = "io.jsonwebtoken:jjwt-jackson:${properties["jsonwebtoken.version"]}")
//    testImplementation(dependencyNotation = "org.springframework.cloud:spring-cloud-starter-contract-verifier")
    // to get Constants
    implementation(dependencyNotation = "org.apache.commons:commons-email:${properties["commons_email.version"]}") {
        exclude(group = "junit")
        exclude(group ="org.easymock")
        exclude(group ="org.powermock")
        exclude(group ="org.slf4j ")
        exclude(group ="commons-io")
        exclude(group ="org.subethamail")
        exclude(group ="com.sun.mail")
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
        freeCompilerArgs = listOf(properties["free_compiler_args_value"].toString())
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



tasks.register<TestReport>(name = "testReport") {
    description = "Generates an HTML test report from the results of testReport task."
    group = "report"
    destinationDir = file(path = "$buildDir/reports/tests")
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
                "features",
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