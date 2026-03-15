package com.min.studioreservation.common.exception

open class CustomException(
    val errorType: ErrorType,
    override val message: String = errorType.message,
    val data: Map<String, Any>? = null
) : RuntimeException(message)
