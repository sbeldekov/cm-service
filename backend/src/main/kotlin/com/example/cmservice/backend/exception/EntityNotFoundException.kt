package com.example.cmservice.backend.exception

import kotlin.reflect.KClass

class EntityNotFoundException(
    val type: KClass<out Any>,
    val id: Any? = null
) : RuntimeException()
