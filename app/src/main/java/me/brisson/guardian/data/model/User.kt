package me.brisson.guardian.data.model


data class User(
    var uid: String = "",
    var token: String? = "",
    var name: String = "",
    var email: String = "",
    var userImage: String? = "",
    var online: Boolean? = false

)
