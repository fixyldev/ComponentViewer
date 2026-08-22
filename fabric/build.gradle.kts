plugins {
    id("java-library")
    id("net.fabricmc.fabric-loom")
}

base.archivesName = "${rootProject.name}-${project.name}"
version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("mod_group").get()

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    implementation(project(path = ":common"))

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

sourceSets.main {
    java.srcDir("../common/src/main/java")
    resources.srcDir("../common/src/main/resources")
}

loom {
    runs.named("client") {
        displayName = "Fabric - Client"
        generateRunConfig = true
    }

    runConfigs.configureEach {
        runDirectory = file("../run")
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

java {
    withSourcesJar()

    toolchain.languageVersion = JavaLanguageVersion.of(25)

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
    val rootProjectName = rootProject.name
    val projectName = project.name
    inputs.property("rootProjectName", rootProjectName)
    inputs.property("projectName", projectName)

    from("../LICENSE")
}

tasks.named<Jar>("sourcesJar") {
    val version = version
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("version" to version)
    }

    from("../LICENSE")
}
