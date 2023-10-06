import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    id("org.springframework.boot") version "3.1.4" apply false
    id("io.spring.dependency-management") version "1.1.3"
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
    signing
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
            pom {
                name.set("responsewrapper-spring-boot-starter")
                description.set("Unify response data format")
                url.set("https://github.com/jetems/responsewrapper-spring-boot-starter")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("deluxebear")
                        name.set("deluxebear")
                        email.set("deluxebear@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/jetems/responsewrapper-spring-boot-starter.git")
                    developerConnection.set("scm:git:https://github.com/jetems/responsewrapper-spring-boot-starter.git")
                    url.set("https://github.com/jetems/responsewrapper-spring-boot-starter")
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
signing {
    //使用环境变量中的PGP_SECRET和PGP_PASSWORD进行签名，否则使用gradle.properties中的配置
    val signingKey = System.getenv("PGP_SECRET")
    val signingPassword = System.getenv("PGP_PASSPHRASE")
    if (!signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications["mavenJava"])
}
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl = uri("https://s01.oss.sonatype.org/service/local/")
            snapshotRepositoryUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            username = System.getenv("SONATYPE_USERNAME") ?: extra["sonatype.username"].toString()
            password = System.getenv("SONATYPE_PASSWORD") ?: extra["sonatype.password"].toString()
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
