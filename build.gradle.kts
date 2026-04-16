plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.13"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
	id("info.solidsoft.pitest") version "1.15.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

testing {
	suites {
		val testIntegration by registering(JvmTestSuite::class) {
			sources {
				kotlin {
					setSrcDirs(listOf("src/testIntegration/kotlin"))
				}
				compileClasspath += sourceSets.main.get().output
				runtimeClasspath += sourceSets.main.get().output
			}
		}
		val testComponent by registering(JvmTestSuite::class) {
			sources {
				kotlin {
					setSrcDirs(listOf("src/testComponent/kotlin"))
				}
				resources {
					setSrcDirs(listOf("src/testComponent/resources"))
				}
				compileClasspath += sourceSets.main.get().output
				runtimeClasspath += sourceSets.main.get().output
			}
		}
	}
}

val testIntegrationImplementation: Configuration by configurations.getting {
	extendsFrom(configurations.implementation.get())
}

val testComponentImplementation: Configuration by configurations.getting {
	extendsFrom(configurations.implementation.get())
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.liquibase:liquibase-core")
	implementation("org.postgresql:postgresql")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
	testImplementation("io.kotest:kotest-assertions-core:5.9.1")
	testImplementation("io.kotest:kotest-property:5.9.1")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("com.h2database:h2")
	testIntegrationImplementation("io.mockk:mockk:1.13.8")
	testIntegrationImplementation("io.kotest:kotest-assertions-core:5.9.1")
	testIntegrationImplementation("io.kotest:kotest-runner-junit5:5.9.1")
	testIntegrationImplementation("com.ninja-squad:springmockk:4.0.2")
	testIntegrationImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
	testIntegrationImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}
	testIntegrationImplementation("org.testcontainers:postgresql:1.19.1")
	testIntegrationImplementation("org.testcontainers:testcontainers:1.19.1")
	testIntegrationImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
	testComponentImplementation("io.cucumber:cucumber-java:7.14.0")
	testComponentImplementation("io.cucumber:cucumber-spring:7.14.0")
	testComponentImplementation("io.cucumber:cucumber-junit:7.14.0")
	testComponentImplementation("io.cucumber:cucumber-junit-platform-engine:7.14.0")
	testComponentImplementation("io.rest-assured:rest-assured:5.3.2")
	testComponentImplementation("org.junit.platform:junit-platform-suite:1.10.0")
	testComponentImplementation("org.testcontainers:postgresql:1.19.1")
	testComponentImplementation("io.kotest:kotest-assertions-core:5.9.1")
	testComponentImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	reports {
		junitXml.required.set(true)
	}
}

tasks.test {
	finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.named<JacocoReport>("jacocoTestReport") {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude("**/GestionDeLivreApplication**")
			}
		})
	)
}

configure<info.solidsoft.gradle.pitest.PitestPluginExtension> {
	junit5PluginVersion.set("1.2.1")
	targetClasses.set(setOf("com.example.gestion_de_livre.domain.*"))
	targetTests.set(setOf("com.example.gestion_de_livre.domain.*"))
	excludedClasses.set(setOf("com.example.gestion_de_livre.GestionDeLivreApplication*"))
	mutationThreshold.set(80)
	outputFormats.set(setOf("HTML", "XML"))
	timeoutConstInMillis.set(10000)
	threads.set(1)
}