subprojects {
    plugins.apply("java-library")

    extensions.configure<BasePluginExtension> {
        archivesName = "${rootProject.name}-${project.name}"
    }

    version = providers.gradleProperty("mod_version").get()
    group = providers.gradleProperty("mod_group").get()

    layout.buildDirectory = rootProject.layout.buildDirectory.dir(project.name)

    tasks.withType<JavaCompile>().configureEach {
        options.release = 25
        options.encoding = "UTF-8"
    }

    tasks.withType<Jar>().configureEach {
        from(rootDir.resolve("LICENSE"))
    }

    extensions.configure<JavaPluginExtension> {
        withSourcesJar()
        toolchain.languageVersion = JavaLanguageVersion.of(25)

        if (project.name == "common") {
            return@configure
        }

        sourceSets.named("main") {
            java.srcDir(rootDir.resolve("common/src/main/java"))
            resources.srcDir(rootDir.resolve("common/src/main/resources"))
        }
    }
}
