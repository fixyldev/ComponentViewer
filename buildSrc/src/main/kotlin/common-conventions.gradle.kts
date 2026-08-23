plugins {
    id("java-library")
}

base.archivesName = "${rootProject.name}-${project.name}"
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

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}
