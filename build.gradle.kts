plugins {
	kotlin("jvm") version "1.4.21"
	id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
	java
	maven
	checkstyle
	id("org.sonarqube") version "3.1.1"
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
	api("org.joml:joml:1.10.0")
	api("org.joml:joml-primitives:1.10.0")

	// Apache Commons Math for Linear Programming
	implementation("org.apache.commons", "commons-math3", "3.6.1")

	// Guava
	implementation("com.google.guava:guava:29.0-jre")

	// Jackson Binary Dataformat for Object Serialization
	api("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
	api("com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor", jacksonVersion)

	// FastUtil for Fast Primitive Collections
	implementation("it.unimi.dsi", "fastutil", "8.2.1")

	// Netty for networking
	implementation("io.netty", "netty-buffer", nettyVersion)

	// Junit 5 for Unit Testing
	testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

checkstyle {
	toolVersion = "8.41"
	configFile = file("${rootDir}/.checkstyle/checkstyle.xml")
	isIgnoreFailures = false
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

sonarqube {
	properties {
		property("sonar.projectKey", "ValkyrienSkies_vs-core")
		property("sonar.organization", "valkyrienskies")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}