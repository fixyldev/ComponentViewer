plugins {
    id("common-conventions")
}

dependencies {
    implementation(project(path = ":common"))
}

java.sourceSets.main {
    java.srcDir(project(":common").file("src/main/java"))
    resources.srcDir(project(":common").file("src/main/resources"))
}
