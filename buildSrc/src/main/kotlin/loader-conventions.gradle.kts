plugins {
    id("common-conventions")
}

dependencies {
    implementation(project(path = ":common"))
}

java.sourceSets.main {
    java.srcDir(rootDir.resolve("common/src/main/java"))
    resources.srcDir(rootDir.resolve("common/src/main/resources"))
}
