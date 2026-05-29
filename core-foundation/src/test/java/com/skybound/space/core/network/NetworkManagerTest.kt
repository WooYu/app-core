package com.skybound.space.core.network

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.http.GET

class NetworkManagerTest {

    private lateinit var server: MockWebServer

    interface TestApi : ApiService {
        @GET("/test")
        suspend fun get(): BaseResponse<String>
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `NetworkManager creates valid Retrofit instance`() {
        val config = NetworkConfig(
            baseUrl = server.url("/").toString(),
            enableLogging = false
        )
        val manager = NetworkManager(config)
        val api = manager.createApi(TestApi::class.java)
        assertNotNull(api)
    }

    @Test
    fun `NetworkManager executes GET request successfully`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"code":200,"message":"ok","data":"hello"}""")
        )
        val config = NetworkConfig(baseUrl = server.url("/").toString())
        val manager = NetworkManager(config)
        val api = manager.createApi(TestApi::class.java)
        val response = api.get()
        assert(response.isSuccess)
        assert(response.data == "hello")
    }
}
