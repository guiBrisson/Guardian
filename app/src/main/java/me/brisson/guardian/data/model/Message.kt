package me.brisson.guardian.data.model

data class Message(
    val id : Int = 0,
    val name : String = "",
    val image : String = "",
    val message: String = "",
    val timeAgo: String = "",
    val newMessages: Int = 0,
    val isGuardian: Boolean = false
)
