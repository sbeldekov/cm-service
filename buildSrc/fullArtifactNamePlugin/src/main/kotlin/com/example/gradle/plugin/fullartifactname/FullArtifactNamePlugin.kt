package com.example.gradle.plugin.fullartifactname

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.withType

class FullArtifactNamePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        var jarBaseName = target.name
        var parentProject = target.parent
        while (parentProject != null) {
            jarBaseName = "${parentProject.name}-$jarBaseName"
            parentProject = parentProject.parent
        }
        target.tasks.withType(Jar::class).configureEach {
            // default value = project.name
            archiveBaseName.set(jarBaseName)
        }
    }
}
