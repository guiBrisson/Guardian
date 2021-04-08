package me.brisson.guardian.data.api

import me.brisson.guardian.data.model.User
import retrofit2.Response

interface ApiHelper {

    suspend fun getUsers(): Response<List<User>>

}