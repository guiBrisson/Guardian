package me.brisson.guardian.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import me.brisson.guardian.data.model.User

class UsersRepository {

    fun updateFirebaseUserName(user: FirebaseUser, name: String): Task<Void> {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        return user.updateProfile(profileUpdates)
    }

    fun addNewUserToUsersCollection(newUser: User): Task<Void> {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        return usersCollection
                .document(newUser.uid) // document path
                .set(newUser)
    }
}