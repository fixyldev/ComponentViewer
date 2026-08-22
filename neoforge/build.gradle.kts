plugins {
    id("java-library")
    id("net.neoforged.moddev")
}

base.archivesName = "${rootProject.name}-${project.name}"
version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("mod_group").get()

dependencies {
    implementation(project(path = ":common"))
}

sourceSets.main {
    java.srcDir("../common/src/main/java")

    resources {
        srcDir("../common/src/main/resources")

        srcDir("src/generated/resources")
        exclude("**/*.bbmodel")
        exclude("src/generated/**/.cache")
    }
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
            gameDirectory = file("../run")

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
    description = "Expands templates with declared mod properties"
    val replaceProperties = mapOf(
        "version" to version,
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main { resources.srcDir(generateModMetadata) }
neoForge.ideSyncTask(generateModMetadata)

java {
    withSourcesJar()

    toolchain.languageVersion = JavaLanguageVersion.of(25)

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    from("../LICENSE")
}

tasks.named<Jar>("sourcesJar") {
    from("../LICENSE")
}
