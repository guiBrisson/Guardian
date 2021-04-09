package me.brisson.guardian.ui.fragments.recent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Notification
import me.brisson.guardian.ui.base.BaseViewModel
import me.brisson.guardian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor() : BaseViewModel() {

    private val recentNotifications = MutableLiveData<Resource<List<Notification>>>()

    fun getRecentNotification() : LiveData<Resource<List<Notification>>> = recentNotifications

    init {
        recentNotifications.postValue(Resource.success(mockNotifications()))
    }

    private fun mockNotifications() : List<Notification> {
        return listOf(
            Notification(
                id = 0,
                image = "",
                text = "Uma mensagem de emergência foi enviada para seus guardians.",
                days = "4 dias",
                date = "25/03/2021"
            ),
            Notification(
                id = 0,
                image = "",
                text = "Uma mensagem de emergência foi enviada para seus guardians.",
                days = "4 dias",
                date = "25/03/2021"
            ),
            Notification(
                id = 0,
                image = "",
                text = "Uma mensagem de emergência foi enviada para seus guardians.",
                days = "4 dias",
                date = "25/03/2021"
            )
        )
    }
}