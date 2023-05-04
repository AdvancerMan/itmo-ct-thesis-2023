plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "ru.itmo.kazakov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-core:5.3.26")
    implementation("org.springframework:spring-context:5.3.26")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("org.uma.jmetal:jmetal-core:6.0")
    implementation("org.uma.jmetal:jmetal-problem:6.0")
    implementation("org.uma.jmetal:jmetal-algorithm:6.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}
