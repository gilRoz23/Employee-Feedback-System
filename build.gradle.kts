//import org.gradle.api.tasks.testing.logging.TestExceptionFormat
//import org.gradle.api.tasks.testing.logging.TestLogEvent
//
//plugins {
//	id("org.flywaydb.flyway") version "10.4.1"
//	kotlin("jvm") version "1.9.25"
//	kotlin("plugin.spring") version "1.9.25"
//	id("org.springframework.boot") version "3.3.3"
//	id("io.spring.dependency-management") version "1.1.6"
//}
//
//buildscript {
//	dependencies {
//		classpath("org.flywaydb:flyway-database-postgresql:10.4.1")
//	}
//}
//
//flyway {
//	url = "jdbc:postgresql://localhost:5432/academy?user=bob&password=dev"
//	driver = "org.postgresql.Driver"
//}
//
//java {
//	toolchain {
//		languageVersion = JavaLanguageVersion.of(21)
//	}
//}
//
//repositories {
//	mavenCentral()
//	google()
//}
//
//dependencies {
//	implementation("org.springframework.boot:spring-boot-starter")
//	implementation("org.springframework.boot:spring-boot-starter-actuator")
//	implementation("org.springframework.boot:spring-boot-starter-jdbc")
//	implementation("org.springframework.boot:spring-boot-starter-jersey")
//	implementation("org.springframework.boot:spring-boot-starter-logging")
//
//	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//	implementation("com.google.guava:guava:33.3.0-jre")
//	implementation("org.apache.commons:commons-lang3:3.16.0")
//	implementation("org.apache.httpcomponents:httpclient:4.5.14")
//	implementation("org.flywaydb:flyway-core")
//	implementation("org.flywaydb:flyway-database-postgresql")
//	implementation("org.jetbrains.kotlin:kotlin-reflect")
//	implementation("org.jooq:jooq:3.17.9")
//	implementation("org.slf4j:slf4j-api")
//
//	implementation("org.postgresql:postgresql")
//
//	testImplementation("org.junit.jupiter:junit-jupiter")
//	testImplementation("org.junit.jupiter:junit-jupiter-params")
//	testImplementation("org.hamcrest:hamcrest-library")
//	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
//	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
//	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
//	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
//	implementation("com.google.cloud:google-cloud-language:2.5.0")
//
//}
//
//
//tasks {
//	withType<Test> {
//		useJUnitPlatform()
//		testLogging {
//			displayGranularity = 1
//			showCauses = true
//			showStackTraces = true
//			exceptionFormat = TestExceptionFormat.FULL
//			events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
//		}
//		reports.junitXml
//		val dbUrl = System.getenv("DB_URL")
//		if (dbUrl != null && dbUrl.isNotBlank()) {
//			systemProperty("DB_URL", dbUrl)
//		} else {
//			systemProperty("DB_URL", "jdbc:postgresql://localhost:5432/academy?user=bob&password=dev")
//		}
//		systemProperty("spring.profiles.active", "development,test")
//	}
//}
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	id("org.flywaydb.flyway") version "10.4.1"
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("nu.studer.jooq") version "8.2" // Add JOOQ plugin
}

buildscript {
	dependencies {
		classpath("org.flywaydb:flyway-database-postgresql:10.4.1")
	}
}

flyway {
	url = "jdbc:postgresql://localhost:5432/academy?user=bob&password=dev"
	driver = "org.postgresql.Driver"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	google()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-jersey")
	implementation("org.springframework.boot:spring-boot-starter-logging")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.google.guava:guava:33.3.0-jre")
	implementation("org.apache.commons:commons-lang3:3.16.0")
	implementation("org.apache.httpcomponents:httpclient:4.5.14")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jooq:jooq:3.17.9")
	implementation("org.slf4j:slf4j-api")

	implementation("org.postgresql:postgresql")
	jooqGenerator("org.postgresql:postgresql:42.6.0") // JOOQ generator dependency

	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.junit.jupiter:junit-jupiter-params")
	testImplementation("org.hamcrest:hamcrest-library")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("com.google.cloud:google-cloud-language:2.5.0")
	implementation("com.squareup.okhttp3:okhttp:4.11.0")
	implementation("org.json:json:20210307")
}

jooq {
	version.set("3.18.0") // JOOQ version
	configurations {
		create("main") { // Task name will be 'generateJooq'
			generateSchemaSourceOnCompilation.set(false) // Prevent auto-generation on every build
			jooqConfiguration.apply {
				jdbc.apply {
					driver = "org.postgresql.Driver"
					url = "jdbc:postgresql://localhost:5432/academy"
					user = "bob"
					password = "dev"
				}
				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						inputSchema = "public"
					}
					target.apply {
						packageName = "com.hibob.academy.generated" // Replace with your package
						directory = "src/main/java" // Output directory
					}
				}
			}
		}
	}
}

tasks {
	withType<Test> {
		useJUnitPlatform()
		testLogging {
			displayGranularity = 1
			showCauses = true
			showStackTraces = true
			exceptionFormat = TestExceptionFormat.FULL
			events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
		}
		reports.junitXml
		val dbUrl = System.getenv("DB_URL")
		if (dbUrl != null && dbUrl.isNotBlank()) {
			systemProperty("DB_URL", dbUrl)
		} else {
			systemProperty("DB_URL", "jdbc:postgresql://localhost:5432/academy?user=bob&password=dev")
		}
		systemProperty("spring.profiles.active", "development,test")
	}
}