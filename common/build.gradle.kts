plugins {
    id("java-library")
    id("net.fabricmc.fabric-loom")
}

dependencies {
    minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")

    compileOnly("net.fabricmc:fabric-loader:${providers.gradleProperty("fabric_loader_version").get()}")
}

loom {
    runConfigs.clear()

    accessWidenerPath = file("../fabric/src/main/resources/componentviewer-fabric.accesswidener")
}
