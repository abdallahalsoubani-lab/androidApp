package com.template.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Feature convention plugin that applies common dependencies for feature modules.
 * Requires the module to be a library module (apply template.android.library first).
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:analytics"))
                add("implementation", project(":core:network"))
                add("implementation", project(":core:database"))
                add("implementation", project(":core:datastore"))

                add("implementation", libs.findLibrary("androidx-navigation-compose").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-compose").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())

                add("testImplementation", project(":core:testing"))
                add("testImplementation", libs.findLibrary("junit").get())
                add("testImplementation", libs.findLibrary("mockk").get())
                add("testImplementation", libs.findLibrary("truth").get())
                add("testImplementation", libs.findLibrary("turbine").get())

                add("androidTestImplementation", libs.findLibrary("androidx-test-espresso-core").get())
                add("androidTestImplementation", libs.findLibrary("androidx-test-junit").get())
            }
        }
    }
}
