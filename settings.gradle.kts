
pluginManagement {
    val kotlinVersion: String by settings
    val springDependencyManagementVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion apply false
        id("io.spring.dependency-management") version springDependencyManagementVersion apply false
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "rent-car-uniteller"
include(
    "rent-car-contract",
    "black-list-contract"
)
