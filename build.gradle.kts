plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation ("org.jsoup:jsoup:1.14.3")
    implementation ("org.postgresql:postgresql:42.2.5")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}