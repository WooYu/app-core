package com.skybound.space.core.network.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenRefresher: suspend () -> String?
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // Avoid infinite retry
        if (response.request.header("Authorization") == null) return null
        return null // App layer handles 401 with coroutine-based token refresh
    }
}
