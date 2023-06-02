package com.example.dispositivosmoviles

@kotlinx.serialization.Serializable
data class UserResponse(
    val info: Info,
    val results: List<Result>
) {
    @kotlinx.serialization.Serializable
    data class Info(
        val page: Int,
        val results: Int,
        val seed: String,
        val version: String
    )

    @kotlinx.serialization.Serializable
    data class Result(
        val cell: String,
        val dob: Dob,
        val email: String,
        val gender: String,
        val id: Id,
        val location: Location,
        val login: Login,
        val name: Name,
        val nat: String,
        val phone: String,
        val picture: Picture,
        val registered: Registered
    ) {
        @kotlinx.serialization.Serializable
        data class Dob(
            val age: Int,
            val date: String
        )

        @kotlinx.serialization.Serializable
        data class Id(
            val name: String,
            val value: String
        )

        @kotlinx.serialization.Serializable
        data class Location(
            val city: String,
            val coordinates: Coordinates,
            val country: String,
            val postcode: Int,
            val state: String,
            val street: Street,
            val timezone: Timezone
        ) {
            @kotlinx.serialization.Serializable
            data class Coordinates(
                val latitude: String,
                val longitude: String
            )

            @kotlinx.serialization.Serializable
            data class Street(
                val name: String,
                val number: Int
            )

            @kotlinx.serialization.Serializable
            data class Timezone(
                val description: String,
                val offset: String
            )
        }

        @kotlinx.serialization.Serializable
        data class Login(
            val md5: String,
            val password: String,
            val salt: String,
            val sha1: String,
            val sha256: String,
            val username: String,
            val uuid: String
        )

        @kotlinx.serialization.Serializable
        data class Name(
            val first: String,
            val last: String,
            val title: String
        )

        @kotlinx.serialization.Serializable
        data class Picture(
            val large: String,
            val medium: String,
            val thumbnail: String
        )

        @kotlinx.serialization.Serializable
        data class Registered(
            val age: Int,
            val date: String
        )
    }
}