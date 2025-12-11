plugins {
    java
}

group = "com.saga"
version = "0.0.1-SNAPSHOT"
description = "common"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
}

tasks.register("prepareKotlinBuildScriptModel"){}