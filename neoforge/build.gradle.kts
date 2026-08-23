plugins {
    id("loader-conventions")
    id("net.neoforged.moddev")
}

neoForge {
    version = providers.gradleProperty("neoforge_version").get()

    validateAccessTransformers = true

    runs {
        create("client") {
            client()
            ideName = "NeoForge - Client"
        }

        create("server") {
            server()
            disableIdeRun()
            programArgument("--nogui")
        }

        configureEach {
            gameDirectory = rootDir.resolve("run")

            systemProperty("neoforge.enabledGameTestNamespaces", rootProject.name)
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods.create(rootProject.name) {
        sourceSet(sourceSets.main.get())
    }
}

expandTemplates {
    expandProperty("version", "mod_version")
}
