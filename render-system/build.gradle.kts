plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":engine"))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}