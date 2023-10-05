import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    id("org.springframework.boot") version "3.1.4" apply false
    id("io.spring.dependency-management") version "1.1.3"
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("kapt") version "1.8.22" // 用于生成 spring-configuration-metadata.json
}

group = "com.jetems"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}


tasks.named<Jar>("jar") {
    enabled = true
    archiveBaseName.set("responsewrapper-spring-boot-starter")
    archiveClassifier.set("") //生成的jar包不包含boot
}
repositories {
    mavenCentral()
}
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "responsewrapper-spring-boot-starter"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
    repositories {
        maven {
            // system.getenv() 用于读取环境变量，如果环境变量不存在，则使用 extra["my.huawei.release.url"] 读取 gradle.properties 中的配置
            val releasesRepoUrl =
                uri(System.getenv("MY_HUAWEI_RELEASE_URL") ?: extra["my.huawei.release.url"].toString())
            val snapshotsRepoUrl =
                uri(System.getenv("MY_HUAWEI_SNAPSHOTS_URL") ?: extra["my.huawei.snapshots.url"].toString())
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("MY_HUAWEI_USERNAME") ?: extra["my.huawei.username"].toString()
                password = System.getenv("MY_HUAWEI_PASSWORD") ?: extra["my.huawei.password"].toString()
            }
        }
    }
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

// 用于生成 spring-configuration-metadata.json
kapt {
    arguments {
        arg(
            "org.springframework.boot.configurationprocessor.additionalMetadataLocations",
            "$projectDir/src/main/resources"
        )
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        /*
         * Prior to Kotlin 1.6 we had `jvm-default=enable`, 1.6.20 adds `-Xjvm-default=all-compatibility`
         *   > .. generate compatibility stubs in the DefaultImpls classes.
         *   > Compatibility stubs could be useful for library and runtime authors to keep backward binary
         *   > compatibility for existing clients compiled against previous library versions.
         * Ref. https://kotlinlang.org/docs/kotlin-reference.pdf
         */
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all-compatibility"
        jvmTarget = "17"
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor") // 用于生成 spring-configuration-metadata.json
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
