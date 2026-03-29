plugins {
    id("java-library")
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "8.4.0"
}

group = "io.github.sanwenyukaochi"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

extra["springBootVersion"] = "4.0.5"
extra["lombokVersion"] = "1.18.42"

dependencies {
    // Spring Boot dependencies
    compileOnly("org.springframework.boot:spring-boot-starter-web:${property("springBootVersion")}")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator:${property("springBootVersion")}")

    // Lombok dependencies
    compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
    annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${property("springBootVersion")}")

    // Lombok dependencies for test classes
    testCompileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
    testAnnotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${property("springBootVersion")}")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-web:${property("springBootVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator:${property("springBootVersion")}")
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
