package me.brisson.guardian.data.model

import java.io.Serializable

data class Contact(
        var uid: String = "",
        var name: String = "",
        var isAdded: Boolean = false,
        var photo: String? = "",
        var phoneNo: String = ""
) : Serializable

