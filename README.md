## Company Management Service (cm-service)

This is just a project to play around with modern backend java/kotlin development stack. Api to manage abstract
companies.

### DONE

* Build
    * Multi-project gradle build
    * Parallel projects build
    * Centralized repositories and dependency management for projects
    * Toolchains setup for projects
    * `Openapi` server code generation for `backend`
    * `Openapi` client code generation for `backend tests`
    * `jacoco` enabled with correct configuration of excludes with pretty summary report
    * `kotlinter` enabled and configured (`ktlint` linter)
    * `maxParallelForks` set to run tests
* Src
    * `EditorConfig` configured
* Dev
    * `Hibernate envers`
    * Spring Data audit
    * Optimistic locking
    * Retry support enabled to recover optimistic locking failures
    * Fail if no spring active profile set while startup
    * `Testcontainers` to prepare env
* Test
    * `Testcontainers` to prepare env

### TODO

* Dev
