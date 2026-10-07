val lwjglVersion = "3.3.4"

plugins {
    java
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

val hostOs = providers.systemProperty("os.name").get().lowercase()
val hostArch = providers.systemProperty("os.arch").get().lowercase()
val lwjglNatives = when {
    hostOs.startsWith("mac") -> when (hostArch) {
        "aarch64", "arm64" -> "natives-macos-arm64"
        "amd64", "x86_64" -> "natives-macos"
        else -> error("Unsupported macOS architecture: $hostArch")
    }
    hostOs.startsWith("windows") -> when (hostArch) {
        "aarch64", "arm64" -> "natives-windows-arm64"
        "amd64", "x86_64" -> "natives-windows"
        else -> error("Unsupported Windows architecture: $hostArch")
    }
    hostOs.startsWith("linux") -> when (hostArch) {
        "aarch64", "arm64" -> "natives-linux-arm64"
        "amd64", "x86_64" -> "natives-linux"
        else -> error("Unsupported Linux architecture: $hostArch")
    }
    else -> error("Unsupported operating system: $hostOs")
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

    // Package only the natives matching the current host.
    listOf("lwjgl", "lwjgl-opengl", "lwjgl-glfw").forEach { module ->
        runtimeOnly("org.lwjgl:$module::$lwjglNatives")
    }

    // === Testing ===
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}
application {
    mainClass.set("com.voxelsandbox.rendersystem.demo.CpuRenderDemo")
    applicationDefaultJvmArgs = listOf("-Djava.awt.headless=true")
}

val nativeJvmArgs = if (hostOs.startsWith("mac")) listOf("-XstartOnFirstThread") else emptyList()
val renderRuntimeClasspath = sourceSets.main.get().runtimeClasspath

tasks.register<JavaExec>("runPreview") {
    group = "application"
    description = "Open a resizable OpenGL window showing the CPU world image (Escape to close)."
    classpath = renderRuntimeClasspath
    mainClass.set("com.voxelsandbox.rendersystem.demo.OpenGLPreviewDemo")
    jvmArgs(nativeJvmArgs)
}

tasks.register<JavaExec>("nativeSmoke") {
    group = "verification"
    description = "Verify real OpenGL context lifecycle and texture pixel readback; requires a desktop session."
    classpath = renderRuntimeClasspath
    mainClass.set("com.voxelsandbox.rendersystem.demo.OpenGLPreviewDemo")
    jvmArgs(nativeJvmArgs)
    args("--smoke")
}
