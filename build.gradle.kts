plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false

    alias(libs.plugins.compose) apply false

    alias(libs.plugins.protobuf) apply false

    alias(libs.plugins.ktlint) apply false

    id("java")
    id("signing")
    id("maven-publish")

    idea
}

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
        plugin("signing")
    }

    group = "org.archivekeep"
    version =
        rootProject.libs.versions.archivekeep
            .get()

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withJavadocJar()
        withSourcesJar()
    }

    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                pom {
                    packaging = "jar"
                    url = "https://archivekeep.com/"

                    licenses {
                        license {
                            name = "GNU Affero General Public License, Version 3"
                            url = "https://www.gnu.org/licenses/agpl-3.0.txt"
                        }
                    }
                    developers {
                        developer {
                            id = "kravemir"
                            name = "Miroslav Kravec"
                            email = "kravec.miroslav@gmail.com"
                        }
                    }
                    scm {
                        connection = "scm:git:https://github.com/archivekeep/archivekeep.git"
                        developerConnection = "scm:git:ssh://git@github.com:archivekeep/archivekeep.git"
                        url = "https://github.com/archivekeep/archivekeep"
                    }
                }

                repositories {
                    maven {
                        name = "LocalOutput"
                        url = uri(rootProject.layout.buildDirectory.dir("maven-publish-output"))
                    }
                    maven {
                        name = "CentralSnapshots"
                        url = uri("https://central.sonatype.com/repository/maven-snapshots/")
                        credentials(PasswordCredentials::class)
                    }
                }
            }
        }
    }

    signing {
        useGpgCmd()

        sign(publishing.publications["maven"])
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
