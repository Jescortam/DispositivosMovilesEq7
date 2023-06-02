package com.example.dispositivosmoviles

@kotlinx.serialization.Serializable
data class SampleCreditCard(
    var card: String,
    var card_formatted: String,
    var expiration_date: String,
    var cvc: String,
    var name: String)