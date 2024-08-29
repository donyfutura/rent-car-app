import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    application
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.wavesenterprise:we-contract-sdk-grpc")
    implementation("com.wavesenterprise:we-contract-sdk-test")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.mockk:mockk")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "com.wavesenterprise.BlackListContractStarterKt"
    }
}

project.setProperty("mainClassName", "com.wavesenterprise.BlackListContractStarterKt")
