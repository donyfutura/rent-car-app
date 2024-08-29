plugins {
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    kotlin("kapt")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    api(project(":rent-car-contract"))
    api(project(":black-list-contract"))

    implementation("com.wavesenterprise:we-tx-observer-starter")
    implementation("com.wavesenterprise:we-starter-contract-client")
    implementation("com.wavesenterprise:we-starter-node-client")
    implementation("com.wavesenterprise:we-starter-atomic")
    implementation("com.wavesenterprise:we-starter-tx-signer")

    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.postgresql:postgresql")
    implementation("io.github.openfeign:feign-core")
    implementation("commons-codec:commons-codec:1.15")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    kapt("org.hibernate.orm:hibernate-jpamodelgen")
}
