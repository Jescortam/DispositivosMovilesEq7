package com.example.dispositivosmoviles

import io.ktor.client.*
import io.ktor.client.request.*

class CreditCardImplementation(
    private val client: HttpClient
) : CreditCardService {
    override suspend fun getCreditCards(): List<SampleCreditCard> {
        return client.get { url("https://api.generadordni.es/v2/bank/card?results=1") }
    }
}