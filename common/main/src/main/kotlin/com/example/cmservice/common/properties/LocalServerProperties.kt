package com.example.cmservice.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

@ConfigurationProperties("local.server")
@ConstructorBinding
@Validated
class LocalServerProperties(

    @Suppress("unused")
    @field:NotEmpty
    val url: String?
)
