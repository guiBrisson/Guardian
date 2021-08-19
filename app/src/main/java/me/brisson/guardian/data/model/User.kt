package me.brisson.guardian.data.model

import android.net.Uri


data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var userImage: String? = null,
    var contacts: List<Contact> = emptyList()

)
