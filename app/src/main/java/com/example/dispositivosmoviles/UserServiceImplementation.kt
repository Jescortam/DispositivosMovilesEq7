package com.example.dispositivosmoviles

import io.ktor.client.*
import io.ktor.client.request.*

class UserServiceImplementation(
    private val client: HttpClient
) : UserService {
    override suspend fun getUser(): UserResponse {
        return client.get { url("https://randomuser.me/api/") }
    }
}