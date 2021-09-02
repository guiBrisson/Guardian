package me.brisson.guardian.ui.activities.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.model.Contact
import me.brisson.guardian.data.model.Message
import me.brisson.guardian.data.model.User
import me.brisson.guardian.ui.base.BaseViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : BaseViewModel() {
    private val user = MutableLiveData<User>()
    private val fromId = Firebase.auth.currentUser!!.uid // current user uid
    private val toId = MutableLiveData<String>() // contact uid
    private val timeStamp = System.currentTimeMillis() // current time
    val messageEditText = MutableLiveData<String>() // message from edit text

    private val db = Firebase.firestore
    private val usersRef = db.collection("users")
    private val chatsRef = db.collection("chats")

    private val anyException = MutableLiveData<Exception>()
    fun getAnyException(): LiveData<Exception> = anyException

    private val contact = MutableLiveData<Contact>()
    fun getContact(): LiveData<Contact> = contact

    private val messages = MutableLiveData<ArrayList<Message>>()
    fun getMessages(): LiveData<ArrayList<Message>> = messages

    fun setContact(contact: Contact) {
        this.contact.value = contact
        toId.value = contact.uid
    }

    init {
        fetchUser()
    }

    private fun fetchUser() {
        usersRef
            .document(fromId)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    user.value = it.toObject(User::class.java)
                    fetchMessages()
                    Log.d(TAG, "fetchUser ${user.value?.name}: Successfully")
                }
            }
            .addOnFailureListener {
                anyException.value = it
                Log.e(TAG, "fetchUser: ", it.cause)
            }
    }

    private fun fetchMessages() {
        if (user.value != null) {
            chatsRef
                .document(fromId)
                .collection(toId.value!!)
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { value, _ -> // The unused value is "error"
                    // Observing document changes
                    val documentChanges = value?.documentChanges

                    if (documentChanges != null) {
                        val messages = ArrayList<Message>()
                        for (doc in documentChanges) {
                            // Checking if document is Type.ADDED
                            if (doc.type == DocumentChange.Type.ADDED) {
                                val message = doc.document.toObject(Message::class.java)

                                messages.add(message)
                            }
                        }
                        this.messages.value = messages
                        Log.d(TAG, "fetchMessages: ${this.messages.value}")
                    }
                }
        }
    }

    fun sendMessage() {
        val message = setNewMessage()
        if (message != null) {
            // Creating document for the user sending the message
            chatsRef
                .document(fromId)
                .collection(toId.value!!)
                .add(message)
                .addOnSuccessListener {
                    lastMessage(message)
                    Log.d(
                        TAG,
                        "sendMessage: Document $fromId created successfully\n" +
                                "Message: ${message.message}"
                    )
                }
                .addOnFailureListener {
                    anyException.value = it
                    Log.e(TAG, "sendMessage: ", it.cause)
                }

            // Creating document for the user receiving the message
            chatsRef
                .document(toId.value!!)
                .collection(fromId)
                .add(message)
                .addOnSuccessListener {
                    lastMessage(message)
                    Log.d(
                        TAG,
                        "sendMessage: Document ${toId.value!!} created successfully\n" +
                                "Message: ${message.message}"
                    )
                }
                .addOnFailureListener {
                    anyException.value = it
                    Log.e(TAG, "sendMessage: ", it.cause)
                }


        }
    }

    // Updating chat last message
    private fun lastMessage(message: Message) {
        usersRef
            .document(toId.value!!)
            .collection("contacts")
            .document(fromId)
            .apply {
                // Updating last contact message
                update("lastMessage", message.message)
                    .addOnSuccessListener {
                        Log.d(TAG, "lastMessage: Success.")
                    }
                    .addOnFailureListener {
                        anyException.value = it
                        Log.e(TAG, "lastMessage: ", it.cause)
                    }
                // Updating last contact message time stamp
                update("lastMessageTimer", message.timeStamp)
                    .addOnSuccessListener {
                        Log.d(TAG, "lastMessageTimer: Success.")
                    }
                    .addOnFailureListener {
                        anyException.value = it
                        Log.e(TAG, "lastMessageTimer: ", it.cause)
                    }
            }

        usersRef
            .document(fromId)
            .collection("contacts")
            .document(toId.value!!)
            .apply {
                // Updating last contact message
                update("lastMessage", message.message)
                    .addOnSuccessListener {
                        Log.d(TAG, "lastMessage: Success.")
                    }
                    .addOnFailureListener {
                        anyException.value = it
                        Log.e(TAG, "lastMessage: ", it.cause)
                    }
                // Updating last contact message time stamp
                update("lastMessageTimer", message.timeStamp)
                    .addOnSuccessListener {
                        Log.d(TAG, "lastMessageTimer: Success.")
                    }
                    .addOnFailureListener {
                        anyException.value = it
                        Log.e(TAG, "lastMessageTimer: ", it.cause)
                    }
            }
    }

    private fun setNewMessage(): Message? {
        return if (!messageEditText.value.isNullOrEmpty()) {
            Message(
                fromId = fromId,
                toId = toId.value!!,
                message = messageEditText.value,
                timeStamp = timeStamp,
            )
        } else {
            null
        }
    }

    companion object {
        private val TAG = ChatViewModel::class.java.simpleName
    }

}