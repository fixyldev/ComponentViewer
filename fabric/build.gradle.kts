plugins {
    id("loader-conventions")
    id("net.fabricmc.fabric-loom") version BuildConfig.FABRIC_LOOM_VERSION
}

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

val modmenuConfiguration =
    if (providers.gradleProperty("dev.fixyl.componentviewer.compileOnlyModmenu")
        .orNull.toBoolean()) { "compileOnly" } else { "implementation" }

dependencies {
    minecraft("com.mojang:minecraft:${BuildConfig.MINECRAFT_VERSION}")

    implementation("net.fabricmc:fabric-loader:${BuildConfig.FABRIC_LOADER_VERSION}")
    implementation("net.fabricmc.fabric-api:fabric-api:${BuildConfig.FABRIC_API_VERSION}")

    add(modmenuConfiguration,
        "com.terraformersmc:modmenu:${BuildConfig.MODMENU_VERSION}")
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
    expand("version", BuildConfig.getVersionString())
}
