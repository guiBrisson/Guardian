package me.brisson.guardian

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(), Application.ActivityLifecycleCallbacks{

    // Setting user online status
    private fun setOnline(boolean: Boolean){
        val uid = FirebaseAuth.getInstance().uid

        if (uid != null){
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .update("online", boolean)
        }
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {
        // Setting user's online status to true when activity is resumed
        setOnline(true)
    }

    override fun onActivityPaused(p0: Activity) {
        // Setting user's online status to false when activity is paused
        setOnline(false)
    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}