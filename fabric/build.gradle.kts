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

val modmenuConfiguration =
    if (providers.gradleProperty("modmenu_compile_only").orNull?.toBoolean() == true) {
        "compileOnly"
    } else {
        "implementation"
    }
val modmenuVersion = providers.gradleProperty("modmenu_version").get()

dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")

    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("fabric_loader_version").get()}")
    implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")

    add(modmenuConfiguration, "com.terraformersmc:modmenu:${modmenuVersion}")
}

loom {
    runConfigs {
        named("client") {
            client()
            displayName = "Fabric - Client"
            generateRunConfig = true
        }

        configureEach {
            runDirectory = rootDir.resolve("run")
        }
    }

    accessWidenerPath = file("src/main/resources/componentviewer-fabric.accesswidener")
}

expandTemplates {
    expandProperty("version", "mod_version")
}
