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

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    description = "Expands templates with declared mod properties."
    group = "mod development/internal"

    val replaceProperties = mapOf(
        "version" to version,
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}

sourceSets.main {
    resources {
        srcDir(generateModMetadata)
        srcDir("src/generated/resources")
        exclude("**/*.bbmodel")
        exclude("src/generated/**/.cache")
    }
}

neoForge.ideSyncTask(generateModMetadata)
