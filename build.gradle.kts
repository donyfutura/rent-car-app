
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

val kotlinVersion: String by project
val jnaVersion: String by project
val jacksonKotlinVersion: String by project

val weSdkBomVersion: String by project

val junitVersion: String by project
val mockkVersion: String by project

plugins {
    kotlin("jvm") apply false
    id("io.spring.dependency-management") apply false
}

allprojects {
    group = "com.wavesenterprise.app"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
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
            mavenBom("com.wavesenterprise:we-sdk-bom:$weSdkBomVersion") {
                bomProperty("kotlin.version", kotlinVersion)
            }
            mavenBom("com.fasterxml.jackson:jackson-bom:$jacksonKotlinVersion")
        }

        dependencies {
            dependency("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonKotlinVersion")
            dependency("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonKotlinVersion")
            dependency("net.java.dev.jna:jna:$jnaVersion")

            dependency("io.mockk:mockk:$mockkVersion")
            dependency("org.junit.jupiter:junit-jupiter-api:$junitVersion")
            dependency("org.junit.jupiter:junit-jupiter-params:$junitVersion")
            dependency("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
        }
    }
}
