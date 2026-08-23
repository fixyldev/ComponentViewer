plugins {
    id("common-conventions")
}

dependencies {
    implementation(project(path = ":common"))
}

java.sourceSets.main {
    val commonProject = project(":common")

    java.srcDir(commonProject.file("src/main/java"))
    resources.srcDir(commonProject.file("src/main/resources"))
    resources.srcDir(commonProject.tasks.named("expandTemplates"))
}
