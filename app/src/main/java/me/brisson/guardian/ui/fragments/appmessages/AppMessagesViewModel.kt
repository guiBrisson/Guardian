package me.brisson.guardian.ui.fragments.appmessages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Message
import me.brisson.guardian.ui.base.BaseViewModel
import me.brisson.guardian.utils.Resource
import javax.inject.Inject

@HiltViewModel
class AppMessagesViewModel @Inject constructor() : BaseViewModel() {
    private val messages = MutableLiveData<Resource<List<Message>>>()

    fun getMessages() : LiveData<Resource<List<Message>>> = messages

    init {
        messages.postValue(Resource.success(mockMessages()))
    }

    private fun mockMessages() : List<Message>{
        return listOf(
            Message(
                name = "Dwayne The Rock",
                image = "https://s2.glbimg.com/Ha2q-YYa3pCWtwM4E51zi_p-POI=/940x523/e.glbimg.com/og/ed/f/original/2019/02/20/blow-dry-bar-del-mar-chairs-counter-853427.jpg",
                message = "Recebi sua mensagem, ta tudo bem?",
                timeAgo = "15 min",
                newMessages = 1,
                isGuardian = true
            ),
            Message(
                name = "Ariana Grande",
                image = "",
                message = "Recebi sua mensagem, ta tudo bem?",
                timeAgo = "5 horas",
                newMessages = 0,
                isGuardian = true
            ),
            Message(
                name = "Mark Ruffalo",
                image = "",
                message = "Recebi sua mensagem, ta tudo bem?",
                timeAgo = "50 min",
                newMessages = 100,
                isGuardian = false
            )

        )
    }
}