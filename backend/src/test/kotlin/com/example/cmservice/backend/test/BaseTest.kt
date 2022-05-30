package com.example.cmservice.backend.test

import com.example.cmservice.common.config.RandomPortContextInitializer
import com.example.cmservice.common.config.TestcontainersContextInitializer
import com.example.cmservice.common.properties.LocalServerProperties
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@ContextConfiguration(
    initializers = [RandomPortContextInitializer::class, TestcontainersContextInitializer::class]
)
@ActiveProfiles(profiles = ["test"])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
abstract class BaseTest {

    @TestConfiguration
    @EnableFeignClients
    @EnableConfigurationProperties(LocalServerProperties::class)
    class TestConfig
}
