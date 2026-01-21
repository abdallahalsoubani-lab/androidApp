package com.template.convention

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = "1.5.9"
                }

                packagingOptions {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }

            dependencies {
                add("implementation", platform(libs.findLibrary("androidx-compose-bom").get()))
                add("implementation", libs.findLibrary("androidx-compose-foundation").get())
                add("implementation", libs.findLibrary("androidx-compose-foundation-layout").get())
                add("implementation", libs.findLibrary("androidx-compose-material3").get())
                add("implementation", libs.findLibrary("androidx-compose-runtime").get())
                add("implementation", libs.findLibrary("androidx-compose-ui").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
                add("androidTestImplementation", platform(libs.findLibrary("androidx-compose-bom").get()))
                add("androidTestImplementation", libs.findLibrary("compose-ui-test-junit4").get())
                add("debugImplementation", libs.findLibrary("compose-ui-test-manifest").get())
            }
        }
    }
}
