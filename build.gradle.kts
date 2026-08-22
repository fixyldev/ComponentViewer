subprojects {
    layout.buildDirectory = rootProject.layout.buildDirectory.dir(project.name)

    tasks.withType<JavaCompile>().configureEach {
        options.release = 25
        options.encoding = "UTF-8"
    }
}
