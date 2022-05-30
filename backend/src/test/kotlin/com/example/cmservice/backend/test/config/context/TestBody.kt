package com.example.cmservice.backend.test.config.context

import javax.validation.constraints.NotEmpty

class TestBody(

    @get:NotEmpty
    val body: String?
)
