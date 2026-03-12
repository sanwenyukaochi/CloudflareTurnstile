plugins {
    id("java-library")
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "8.3.0"
}

group = "io.github.sanwenyukaochi"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

val springBootVersion by extra("4.0.3")
val lombokVersion by extra("1.18.42")

dependencies {
    // Spring Boot dependencies
    compileOnly("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")

    // Lombok dependencies
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")

    // Lombok dependencies for test classes
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveBaseName.set("sanwenyukaochi-cloudflare-turnstile")
    archiveClassifier.set("")
}

spotless {
    encoding("UTF-8")
    java {
        palantirJavaFormat()
        importOrder()
        removeUnusedImports()
        formatAnnotations()
        trimTrailingWhitespace()
        endWithNewline()
        toggleOffOn()
    }

    kotlin {
        ktlint()
    }

    kotlinGradle {
        ktlint()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("compileJava") {
    dependsOn(tasks.named("spotlessCheck"))
}
