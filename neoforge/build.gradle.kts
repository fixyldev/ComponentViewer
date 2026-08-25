plugins {
    id("loader-conventions")
    id("net.neoforged.moddev") version BuildConfig.MODDEV_VERSION
}

neoForge {
    version = BuildConfig.NEOFORGE_VERSION

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

            systemProperty("neoforge.enabledGameTestNamespaces", BuildConfig.MOD_ID)
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods.create(BuildConfig.MOD_ID) {
        sourceSet(sourceSets.main.get())
    }
}

expandTemplates {
    expand("version", BuildConfig.getVersionString())
}
