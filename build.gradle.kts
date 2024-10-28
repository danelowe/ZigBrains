import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    kotlin("jvm") version "1.9.24" apply false
    id("org.jetbrains.intellij.platform") version "2.1.0"
    id("org.jetbrains.changelog") version "2.2.1"
    id("org.jetbrains.grammarkit") version "2022.3.2.2" apply false
    idea
}

val javaVersion = providers.gradleProperty("javaVersion").get().toInt()

group = "com.falsepattern"
version = providers.gradleProperty("pluginVersion").get()

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.intellij.platform.module")
    apply(plugin = "idea")

    extensions.configure<KotlinJvmProjectExtension>("kotlin") {
        jvmToolchain(javaVersion)
    }
}

tasks {
    processResources {
        from("LICENSE")
        from("licenses") {
            into("licenses")
        }
    }
}

allprojects {
    idea {
        module {
            isDownloadJavadoc = false
            isDownloadSources = true
        }
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(javaVersion)
            @Suppress("UnstableApiUsage")
            vendor = JvmVendorSpec.JETBRAINS
        }
        sourceCompatibility = JavaVersion.toVersion(javaVersion)
        targetCompatibility = JavaVersion.toVersion(javaVersion)
    }

    repositories {
        mavenCentral()

        intellijPlatform {
            defaultRepositories()
        }
    }
    dependencies {
        intellijPlatform {
            instrumentationTools()
        }
    }
}

dependencies {
    intellijPlatform {
        create(IntelliJPlatformType.IntellijIdeaCommunity, providers.gradleProperty("ideaCommunityVersion"))

        pluginVerifier()
        zipSigner()
    }

    implementation(project(":zig"))
    implementation(project(":zon"))
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")

        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog

        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }

    signing {
        certificateChainFile = file("secrets/chain.crt")
        privateKeyFile = file("secrets/private.pem")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        channels = providers.gradleProperty("pluginVersion").map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
    buildSearchableOptions = false
    instrumentCode = false
}

changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

tasks {
    publishPlugin {
        dependsOn(patchChangelog)
    }
    compileJava {
        enabled = false
    }
    classes {
        enabled = false
    }
}