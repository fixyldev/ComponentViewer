plugins {
    id("loader-conventions")
    id("net.fabricmc.fabric-loom")
}

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")

    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("fabric_loader_version").get()}")
    implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")

    val modmenuVersion = providers.gradleProperty("modmenu_version").get()
    if (providers.gradleProperty("modmenu_compile_only").orNull?.toBoolean() == true) {
        compileOnly("com.terraformersmc:modmenu:${modmenuVersion}")
    } else {
        implementation("com.terraformersmc:modmenu:${modmenuVersion}")
    }
}

loom {
    runs.named("client") {
        displayName = "Fabric - Client"
        generateRunConfig = true
    }

    runConfigs.configureEach {
        runDirectory = rootDir.resolve("run")
    }

    accessWidenerPath = file("src/main/resources/componentviewer-fabric.accesswidener")
}

tasks.processResources {
    val version = version
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}

tasks.named<Jar>("sourcesJar") {
    val version = version
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }
}
