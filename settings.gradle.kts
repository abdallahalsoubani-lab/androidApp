pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "android-template"

// App
include(":app")

// Core modules
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:common")
include(":core:analytics")
include(":core:testing")

// Feature modules
include(":feature:auth")
include(":feature:template")
