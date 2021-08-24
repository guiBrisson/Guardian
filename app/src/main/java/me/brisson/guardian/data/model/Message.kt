package me.brisson.guardian.data.model

data class Message(
    val fromId: String = "",
    val toId: String = "",
    val message: String? = "",
    val timeStamp: Long? = 0
)
