package com.example.gradle.plugin.jacocoextra

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.tasks.JacocoReport

class JacocoExtraPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val helloPluginExtension = target.extensions.create(
            "JacocoExtraPluginOptions", JacocoExtraPluginExtension::class.java
        )

        target.tasks.withType<JacocoReport>().configureEach {
            dependsOn(target.tasks.withType<Test>())
            reports.csv.required.set(false)
            reports.html.required.set(false)
            reports.xml.required.set(false)
            // https://stackoverflow.com/questions/29887805/filter-jacoco-coverage-reports-with-gradle
            classDirectories.setFrom(
                target.files(classDirectories.files.map {
                    target.fileTree(it) {
                        exclude(helloPluginExtension.excludes.get())
                    }
                })
            )
        }
    }
}

abstract class JacocoExtraPluginExtension {
    abstract val excludes: SetProperty<String>

    init {
        @Suppress("LeakingThis")
        excludes.convention(emptySet())
    }
}
