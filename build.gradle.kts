plugins {
    java
    application
    kotlin("jvm") version "1.4.30"
}

group = "is.ulrik"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.scijava.org/content/groups/public")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("cisd:jhdf5:19.04.0")
    testCompile("junit", "junit", "4.12")
}

application {
    mainClass.set("HDF5CompositeDump")
}