plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

group = "com"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("net.javacrumbs.shedlock:shedlock-spring:6.9.0")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:6.9.0")

    // Spring Web Services (estándar para SOAP sin generación de clases)
    implementation("org.springframework.ws:spring-ws-core:4.0.5")
    implementation("org.springframework.ws:spring-ws-support:4.0.5")
    implementation("org.springframework:spring-oxm:6.0.13")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("com.google.code.gson:gson:2.13.1")

    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.78.1")
    implementation("org.bouncycastle:bcutil-jdk18on:1.78.1")

    implementation("net.sf.jasperreports:jasperreports:7.0.3")
    implementation("net.sf.jasperreports:jasperreports-pdf:7.0.3")
    implementation("net.sf.jasperreports:jasperreports-fonts:7.0.3")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}