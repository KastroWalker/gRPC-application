import com.google.protobuf.gradle.*
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("org.jetbrains.kotlin.kapt") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("io.micronaut.application") version "3.2.1"
    id("com.google.protobuf") version "0.8.15"
}

version = "0.1"
group = "dev.kastro"

val kotlinVersion= project.properties["kotlinVersion"]

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.grpc:micronaut-grpc-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("io.grpc:grpc-kotlin-stub:1.2.1")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation("io.micronaut:micronaut-validation")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass.set("dev.kastro.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/grpckt")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        // for apple m1, please add protoc_platform=osx-x86_64 in $HOME/.gradle/gradle.properties
        artifact = if (project.hasProperty("protocPlatform")) {
            val protocPlatform= project.properties["protocPlatform"]
            "com.google.protobuf:protoc:3.17.2:${protocPlatform}"
        } else {
            "com.google.protobuf:protoc:3.17.2"
        }
    }
    plugins {
        id("grpc") {
            artifact = if (project.hasProperty("protocPlatform")) {
                val protocPlatform= project.properties["protocPlatform"]
                "io.grpc:protoc-gen-grpc-java:1.39.0:${protocPlatform}"
            } else {
                "io.grpc:protoc-gen-grpc-java:1.39.0"
            }
        }
        id("grpckt") {
           artifact = "io.grpc:protoc-gen-grpc-kotlin:0.2.0:jdk7@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
                id("grpckt")
            }
        }
    }
}
micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("dev.kastro.*")
    }
}


