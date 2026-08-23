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

val expandTemplatesExtension = extensions.create<ExpandTemplatesExtension>("expandTemplates")
val expandTemplates = tasks.register<Copy>("expandTemplates") {
    description = "Expand templates with specified values."

    val expansions = expandTemplatesExtension.expansions
    inputs.property("expansions", expansions)
    from("src/main/templates") {
        expand(expansions.get())
    }
    into(layout.buildDirectory.dir("generated/resources/expandTemplates"))
}

java {
    sourceSets.main {
        resources.srcDir(expandTemplates)
    }
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}
