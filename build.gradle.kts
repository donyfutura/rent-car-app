
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project
val jnaVersion: String by project
val jacksonKotlinVersion: String by project

val weSdkBomVersion: String by project

val junitVersion: String by project
val mockkVersion: String by project

val springBootVersion: String by project
val springCloudVersion: String by project
val springCloudSecurityVersion: String by project
val jacksonDatabindVersion: String by project
val postgresVersion: String by project
val feignCoreVersion: String by project
val springdocoOpenapiStarterVersion: String by project
val mavenUser: String? by project
val mavenPassword: String? by project

plugins {
    kotlin("jvm") apply false
    kotlin("plugin.spring") apply false
    kotlin("plugin.jpa") apply false
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
}

allprojects {
    group = "com.wavesenterprise.app"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            name = "maven-snapshots"
            url = uri("https://artifacts.wavesenterprise.com/repository/maven-snapshots/")
            mavenContent {
                snapshotsOnly()
            }
            credentials {
                username = mavenUser
                password = mavenPassword
            }
        }

        maven {
            name = "maven-releases"
            url = uri("https://artifacts.wavesenterprise.com/repository/maven-releases/")
            mavenContent {
                releasesOnly()
            }
            credentials {
                username = mavenUser
                password = mavenPassword
            }
        }
    }
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }

    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion") {
                bomProperty("kotlin.version", kotlinVersion)
            }
            mavenBom("com.wavesenterprise:we-sdk-bom:$weSdkBomVersion") {
                bomProperty("kotlin.version", kotlinVersion)
            }
            mavenBom("com.fasterxml.jackson:jackson-bom:$jacksonKotlinVersion")
        }

        dependencies {

            dependency("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonKotlinVersion")
            dependency("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVersion")
            dependency("net.java.dev.jna:jna:$jnaVersion")
            dependency("org.postgresql:postgresql:$postgresVersion")
            dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocoOpenapiStarterVersion")

            dependency("io.mockk:mockk:$mockkVersion")
            dependency("org.junit.jupiter:junit-jupiter-api:$junitVersion")
            dependency("org.junit.jupiter:junit-jupiter-params:$junitVersion")
            dependency("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all-compatibility")
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}
