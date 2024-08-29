import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("com.wavesenterprise:we-contract-sdk-grpc")
    implementation("com.wavesenterprise:we-contract-sdk-test")
    implementation("com.wavesenterprise:wrc10-contract-api")
    implementation("com.wavesenterprise:wrc10-contract-app")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.mockk:mockk")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
    manifest {
        attributes["Main-Class"] = "com.wavesenterprise.RentCarContractStarterKt"
    }
}

project.setProperty("mainClassName", "com.wavesenterprise.RentCarContractStarterKt")
