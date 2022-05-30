gradlePlugin {
    plugins {
        register("fullArtifactNamePlugin") {
            id = "com.example.gradle.plugin.fullartifactname"
            implementationClass = "com.example.gradle.plugin.fullartifactname.FullArtifactNamePlugin"
        }
    }
}
