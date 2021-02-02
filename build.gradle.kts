plugins {
    kotlin("jvm") version "1.4.21"
    maven
}

group = "org.valkyrienskies.core"
version = "1.0"

repositories {
    mavenCentral()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
}

val gdxVersion = "1.9.11"
val jacksonVersion = "2.12.1"
val nettyVersion = "4.1.25.Final"

dependencies {
    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    // Bullet for Physics
    implementation("com.badlogicgames.gdx:gdx-bullet:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-bullet-platform:$gdxVersion:natives-desktop")

    // JOML for Math
    implementation("org.joml:joml:1.10.0")
    implementation("org.joml:joml-primitives:1.10.0")

    // Apache Commons Math for Linear Programming
    implementation("org.apache.commons", "commons-math3", "3.6.1")

    // Guava
    implementation("com.google.guava:guava:29.0-jre")

    // Jackson Binary Dataformat for Object Serialization
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", jacksonVersion)

    // Trove for Fast Primitive Collections
    implementation("net.sf.trove4j", "trove4j", "3.0.3")

    // Netty for networking
    implementation("io.netty", "netty-buffer", nettyVersion)

    // Junit 5 for Unit Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}