package me.brisson.guardian.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
//todo: chat message notifications
class FirebaseCloudMessagingService:  FirebaseMessagingService() {

    // Updating user's token
    override fun onNewToken(p0: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null){
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("token", p0)
        }
        super.onNewToken(p0)
    }
}