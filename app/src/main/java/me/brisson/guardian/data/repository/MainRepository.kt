package me.brisson.guardian.data.repository

import me.brisson.guardian.data.api.ApiHelper
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun getUsers() = apiHelper.getUsers()
}