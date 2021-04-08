package me.brisson.guardian.data.api

import me.brisson.guardian.data.model.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    fun getUsers(): Response<List<User>>

}