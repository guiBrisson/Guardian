package me.brisson.guardian.data.model

import java.io.Serializable

data class Contact(
        var uid: String = "",
        var name: String = "",
        @field:JvmField
        var isPhoneContact: Boolean = false,
        var photo: String? = "",
        var phoneNo: String = ""
) : Serializable

