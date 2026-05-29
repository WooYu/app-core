package com.skybound.space.core.network

sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable? = null) :
        ApiException("Network error: $message", cause)
    class ServerException(val code: Int, message: String) :
        ApiException("Server error $code: $message")
    class UnauthorizedException : ApiException("Unauthorized — token expired or invalid")
    class UnknownException(cause: Throwable) : ApiException("Unknown error", cause)
}
