package com.example.cmservice.common.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@Target(CLASS, FUNCTION)
annotation class ExcludeFromJacocoGeneratedReport
