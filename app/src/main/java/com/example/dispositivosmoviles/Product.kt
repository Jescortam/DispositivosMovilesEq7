package com.example.dispositivosmoviles

data class Product(
    val code: String,
    val name: String,
    val image: String?,
    val imageDrawable: Int?,
    val price: Double,
    var quantity: Double
)