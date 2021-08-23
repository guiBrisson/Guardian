package me.brisson.guardian.data.model

import java.io.Serializable

data class Contact(
        var uid: String = "",
        var name: String = "",
        @field:JvmField
        var isPhoneContact: Boolean = false,
        var photo: String? = "",
        var phoneNo: String? = "",
        @field:JvmField
        var isGuardian: Boolean = false,

        // Message related
        val lastMessage: String? = "",
        val lastMessageTimer: String? = "",
        val newMessagesCount: Int? = 0,

) : Serializable

