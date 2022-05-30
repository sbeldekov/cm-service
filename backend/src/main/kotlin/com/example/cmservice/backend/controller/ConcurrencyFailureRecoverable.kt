package com.example.cmservice.backend.controller

import org.springframework.dao.ConcurrencyFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable

@Retryable(
    include = [ConcurrencyFailureException::class],
    maxAttemptsExpression = "\${concurrency.failure.max.attempts:3}",
    backoff = Backoff(100)
)
interface ConcurrencyFailureRecoverable
