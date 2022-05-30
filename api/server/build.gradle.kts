import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

tasks.register<GenerateTask>("apiServerGenerate") {
    description = "Generates server api code"

    val pkg = "${project.group}.cmservice.generated.server"
    packageName.set(pkg)
    apiPackage.set("$pkg.api")
    invokerPackage.set("$pkg.invoker")
    modelPackage.set("$pkg.model")

    configOptions.put("configPackage", "$pkg.config")
    configOptions.put("library", "spring-boot")
    configOptions.put("performBeanValidation", "true")
    configOptions.put("useBeanValidation", "true")
}
