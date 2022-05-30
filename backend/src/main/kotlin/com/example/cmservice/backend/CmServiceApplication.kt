package com.example.cmservice.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class CmServiceApplication

fun main(args: Array<String>) {
    runApplication<CmServiceApplication>(*args)
}
