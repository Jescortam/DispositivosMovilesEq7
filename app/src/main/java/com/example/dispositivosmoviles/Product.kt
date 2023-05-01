package com.example.dispositivosmoviles

data class Product(
    val code: String,
    val name: String,
    val image: String,
    val price: Number,
    var quantity: Int
)