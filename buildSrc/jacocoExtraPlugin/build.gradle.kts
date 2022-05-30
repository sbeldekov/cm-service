gradlePlugin {
    plugins {
        register("jacocoExtraPlugin") {
            id = "com.example.gradle.plugin.jacocoextra"
            implementationClass = "com.example.gradle.plugin.jacocoextra.JacocoExtraPlugin"
        }
    }
}
