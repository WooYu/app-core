import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named

class AndroidPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("maven-publish")

            val libs = extensions.getByType(
                org.gradle.api.artifacts.VersionCatalogsExtension::class.java
            ).named("libs")
            val coreVersion = libs.findVersion("app-core").get().requiredVersion

            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("release") {
                        groupId = "com.skybound.space"
                        artifactId = project.name
                        version = coreVersion
                    }
                }
                repositories {
                    mavenLocal()
                }
            }

            afterEvaluate {
                extensions.configure<PublishingExtension> {
                    publications.named<MavenPublication>("release") {
                        val componentName = if (components.names.contains("release")) "release" else "javaPlatform"
                        from(components.getByName(componentName))
                    }
                }
            }
        }
    }
}
