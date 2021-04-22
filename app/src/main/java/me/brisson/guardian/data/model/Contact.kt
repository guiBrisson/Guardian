package me.brisson.guardian.data.model

import java.io.Serializable

data class Contact(
        var id: String = "",
        var name: String = "",
        var photo: String? = "",
        var phoneNo: String = ""
) : Serializable

