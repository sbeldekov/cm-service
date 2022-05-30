import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

tasks.register<GenerateTask>("apiClientGenerate") {
    description = "Generates client api code"

    val pkg = "${project.group}.cmservice.generated.client"
    packageName.set(pkg)
    apiPackage.set("$pkg.api")
    invokerPackage.set("$pkg.invoker")
    modelPackage.set("$pkg.model")

    configOptions.put("configPackage", "$pkg.config")
    configOptions.put("library", "spring-cloud")
    configOptions.put("performBeanValidation", "false")
    configOptions.put("useBeanValidation", "false")
}
