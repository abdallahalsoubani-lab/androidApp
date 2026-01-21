package com.template.convention

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }

            configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
                jvmToolchain(17)
            }
        }
    }
}
