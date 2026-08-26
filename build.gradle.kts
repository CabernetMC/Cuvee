plugins {
    `java-library`
    `maven-publish`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.annotations)

    api(libs.gson)

    testImplementation(platform(libs.junit.platform))
    testImplementation(libs.junit.jupiter)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            this.groupId = rootProject.group.toString()
            this.version = rootProject.version.toString()
            from(components["java"])
        }
    }

    repositories {
        maven("https://maven.miles.sh/snapshots") {
            credentials {
                this.username = System.getenv("REPO_USERNAME")
                this.password = System.getenv("REPO_PASSWORD")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
