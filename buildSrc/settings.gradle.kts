// https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:centralized-repository-declaration
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}

include("fullArtifactNamePlugin", "jacocoExtraPlugin")
