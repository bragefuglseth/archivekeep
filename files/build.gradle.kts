plugins {
    id("java-library")

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.protobuf)

    alias(libs.plugins.ktlint)
}

group = "org.archivekeep"
version = libs.versions.archivekeep.get()

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.stub)
    implementation(libs.grpc.netty.shaded)

    api(libs.grpc.kotlin.stub)

    implementation(libs.protobuf.kotlin)

    implementation(libs.kfswatch)

    compileOnly(libs.apache.tomcat.annotations)

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.25.3" }

    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}

ktlint {
    filter {
        exclude { element ->
            val path = element.file.path

            path.contains("/generated/")
        }
    }
}

publishing {
    publications.named<MavenPublication>("maven") {
        artifactId = "archivekeep-files"

        pom {
            name = "ArchiveKeep Files"
            description = "Library providing files management and core functionality, and base API(s)."
        }
    }
}
