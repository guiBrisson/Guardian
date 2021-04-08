package me.brisson.guardian.data.model

import com.squareup.moshi.Json

data class User(
    @Json(name = "_id")
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    @Json(name = "_v")
    val v: Int = 0
)
