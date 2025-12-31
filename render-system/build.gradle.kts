val lwjglVersion = "3.3.4"

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
    // === Engine dependency ===
    implementation(project(":engine"))

    // === LWJGL BOM ===
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    // === LWJGL Core ===
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-glfw")

    // === Native bindings (OS-specific) ===
    runtimeOnly("org.lwjgl:lwjgl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-windows")

    runtimeOnly("org.lwjgl:lwjgl::natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-linux")

    runtimeOnly("org.lwjgl:lwjgl::natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-opengl::natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-macos")

    // === Testing ===
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}