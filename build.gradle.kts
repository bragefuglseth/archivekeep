import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.kotlin.kapt") version "1.9.0"

    application

    id("org.graalvm.buildtools.native") version "0.10.0"

    id("org.asciidoctor.jvm.convert") version "4.0.2"
}

group = "org.archivekeep"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("info.picocli:picocli:4.7.5")
    kapt("info.picocli:picocli-codegen:4.7.5")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
    }
}

application {
    mainClass = "org.archivekeep.cli.MainKt"
}

tasks.withType(Jar::class).configureEach {
    manifest {
        attributes["Main-Class"] = "org.archivekeep.cli.MainKt"
    }
}

graalvmNative {
    toolchainDetection = true
}

val generateManpageAsciiDoc = tasks.register<JavaExec>("generateManpageAsciiDoc") {
    dependsOn(tasks.named("classes"))

    group = "Documentation"
    description = "Generate AsciiDoc manpage"

    classpath(configurations.named("compileClasspath"), configurations.named("kapt"), sourceSets.main.get().compileClasspath)
    mainClass = "picocli.codegen.docgen.manpage.ManPageGenerator"

    args("org.archivekeep.cli.MainCommand", "--outdir=${project.layout.buildDirectory}/generated-picocli-docs", "-v")
}


tasks {
    "asciidoctor"(AsciidoctorTask::class) {
        dependsOn(generateManpageAsciiDoc)

        sourceDir(file("${project.layout.buildDirectory}/generated-picocli-docs"))
        setOutputDir(file("${project.layout.buildDirectory}/docs"))

        logDocuments = true

        outputOptions {
            backends("manpage", "html5")
        }
    }
}
