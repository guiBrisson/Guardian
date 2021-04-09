package me.brisson.guardian.ui.fragments.historic

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Notification
import me.brisson.guardian.ui.base.BaseViewModel
import me.brisson.guardian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class HistoricViewModel @Inject constructor() : BaseViewModel() {
    private val historicNotifications = MutableLiveData<Resource<List<Notification>>>()

    fun getHistoricNotification() : LiveData<Resource<List<Notification>>> = historicNotifications

    init {
        historicNotifications.postValue(Resource.success(mockNotifications()))
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