plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.allopen") version "2.2.21"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-langchain4j-bom:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-mcp-server-bom:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-container-image-podman")
    implementation("io.quarkus:quarkus-kubernetes")
    implementation("io.quarkiverse.bucket4j:quarkus-bucket4j:1.0.7")
    implementation("io.quarkus:quarkus-container-image-buildpack")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-kubernetes-config")
    implementation("io.quarkiverse.langchain4j:quarkus-langchain4j-ollama")
    implementation("io.quarkiverse.langchain4j:quarkus-langchain4j-hugging-face")
    implementation("io.quarkiverse.langchain4j:quarkus-langchain4j-core")
    implementation("io.quarkiverse.langchain4j:quarkus-langchain4j-tavily")
    implementation("io.quarkiverse.langchain4j:quarkus-langchain4j-agentic")
    implementation("io.quarkiverse.langchain4j:quarkus-langchain4j-chatbot")
    implementation("io.quarkiverse.mcp:quarkus-mcp-server-sse")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    testImplementation("io.quarkus:quarkus-junit5")
}

group = "no.twingine.mco"
version = "0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}
allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        javaParameters = true
    }
}
