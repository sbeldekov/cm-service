package com.example.cmservice.backend.test.company

import com.example.cmservice.generated.client.model.CompanyCreateDto
import com.example.cmservice.generated.client.model.CompanyDto
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.Named.named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

object CompanyCreateSuccessProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
        randomAlphabetic(12, 12).lowercase().let {
            Stream.of(
                "foo-$it".let { name -> arguments(named("name:$name", CompanyCreateDto().name(name))) },
                "bar-$it".let { name -> arguments(named("name:$name", CompanyCreateDto().name(name))) }
            )
        }
}

object CompanyCreateInvalidPayloadProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
        Stream.of(
            arguments(named("empty object", CompanyCreateDto())),
            arguments(named("invalid company name: min", CompanyCreateDto().name(""))),
            arguments(named("invalid company name: max", CompanyCreateDto().name(LONG_NAME)))
        )
}

object CompanyInvalidPayloadProvider : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
        Stream.of(
            arguments(named("empty object", CompanyDto())),
            arguments(named("invalid company name: min", CompanyDto().id(1).name(""))),
            arguments(named("invalid company name: max", CompanyDto().id(1).name(LONG_NAME))),
            arguments(named("invalid company id: min", CompanyDto().id(0).name("valid")))
        )
}

private val LONG_NAME = "x".repeat(101)
