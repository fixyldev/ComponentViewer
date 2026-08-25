pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "componentviewer"

include("common", "fabric")

if (providers.gradleProperty("dev.fixyl.componentviewer.includeNeoforge")
    .orNull.toBoolean()) {
    include("neoforge")
}
