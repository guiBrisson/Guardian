package me.brisson.guardian.ui.activities.editprofile

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.ui.base.BaseViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor() : BaseViewModel() {
    val photo = MutableLiveData<Uri>()
    val photoBitmap = MutableLiveData<Bitmap>()
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val phoneNumber = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()

    val nameError = MutableLiveData<Boolean>().apply { value = false }
    val emailError = MutableLiveData<Boolean>().apply { value = false }
    val newPasswordError = MutableLiveData<Boolean>().apply { value = false }

    private val anyError = MutableLiveData<Boolean?>()
    fun getAnyError(): LiveData<Boolean?> = anyError

    private val anyException = MutableLiveData<Exception>()
    fun getAnyException(): LiveData<Exception> = anyException

    val changePassword = MutableLiveData<Boolean>().apply { value = false }

    private val reAuthRequest = MutableLiveData<Boolean>()
    fun getReAuthRequest(): LiveData<Boolean> = reAuthRequest

    private val user = Firebase.auth.currentUser
    fun getUser(): FirebaseUser? = user

    private var db = Firebase.firestore
    private val usersReference = db.collection("users").document(user!!.uid)

    fun onChangePasswordClick() { changePassword.value = !changePassword.value!! }

    // Changing password in userAuth and users collection
    fun changePassword() {
        user!!.updatePassword(newPassword.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reAuthRequest.value = false
                    Log.d(TAG, "User password updated.")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        reAuthRequest.value = true
                        anyException.value = e
                    } catch (e: Exception) {
                        anyError.value = true
                        anyException.value = e
                        Log.w(TAG, e.message!!)
                    }
                }
            }
    }

    fun changePhoneNumber() {
        //todo ???
    }

    // Changing name in userAuth and users collection
    fun changeUserName() {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name.value!!)
            .build()
        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reAuthRequest.value = false

                    // Updating the user name in the "users" collections.
                    usersReference.update("name", name.value)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener {
                                e -> Log.w(TAG, "Error updating document", e)
                        }

                    Log.d(TAG, "User name updated.")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        reAuthRequest.value = true
                    } catch (e: Exception) {
                        anyException.value = e
                        anyError.value = true
                        Log.e(TAG, e.message!!)
                    }
                }
            }
    }

    // Changing email in userAuth and users collection
    fun changeUserEmail() {
        user!!.updateEmail(email.value!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    reAuthRequest.value = false

                    // Updating the user email in the "users" collections.
                    usersReference.update("email", email.value)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            anyException.value = e
                            Log.w(TAG, "Error updating document", e)
                        }

                    Log.d(TAG, "User email updated")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        reAuthRequest.value = true
                    } catch (e: Exception) {
                        anyException.value = e
                        anyError.value = true
                        Log.e(TAG, e.message!!)
                    }

                }
            }
    }

    // Re-authentication when necessary
    fun reAuth(currentPassword: String) {
        val credential = EmailAuthProvider
            .getCredential(user!!.email!!, currentPassword)

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User re-authenticated.")
                } else {
                    anyException.value = task.exception
                    Log.w(TAG, "User re-authenticated failed.", task.exception)
                }
            }
    }

    // Handling image bitmap
    fun handleImageUpload(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val uid = user?.uid

        val reference = FirebaseStorage.getInstance().reference
            .child("profileImages")
            .child("$uid.jpeg")

        reference.putBytes(byteArrayOutputStream.toByteArray())
            .addOnSuccessListener {
                getDownloadUrl(reference)
            }
            .addOnFailureListener {
                Log.e(TAG, "onFailure: ", it.cause)
            }

    }

    // Getting the user image url
    private fun getDownloadUrl(reference: StorageReference) {
        reference.downloadUrl
            .addOnSuccessListener {
                Log.d(TAG, "getDownloadUrl Success: $it")
                setUserProfileImageUrl(it)
            }
    }

    // Updating the user image in the "users" collections.
    private fun setUserProfileImageUrl(uri: Uri) {
        val request = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()

        user!!.updateProfile(request)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    usersReference.update("userImage", uri.toString())
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                        }
                        .addOnFailureListener { e ->
                            anyException.value = e
                            Log.w(TAG, "Error updating document", e)
                        }

                    Log.d(TAG, "setUserProfileUrl: Successfully")
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthRecentLoginRequiredException) {
                        reAuthRequest.value = true
                    } catch (e: Exception) {
                        anyException.value = e
                        anyError.value = true
                        Log.e(TAG, e.message!!)
                    }
                }
            }

    }

    companion object {
        private val TAG = EditProfileViewModel::class.java.simpleName
    }
}