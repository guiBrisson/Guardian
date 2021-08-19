package me.brisson.guardian.data.model

import android.graphics.Bitmap


data class User(
    var uid: String = "",
    var name: String = "",
    var userImage: Bitmap? = null,
    var contacts: List<Contact> = emptyList()

)
