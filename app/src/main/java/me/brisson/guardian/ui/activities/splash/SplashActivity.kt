package me.brisson.guardian.ui.activities.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivitySplashBinding
import me.brisson.guardian.ui.activities.firstscreen.FirstScreenActivity
import me.brisson.guardian.ui.activities.main.MainActivity
import me.brisson.guardian.ui.base.BaseActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private var timeOut: Long = 2000

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.viewModel = viewModel

        Handler(Looper.myLooper()!!).postDelayed({
            if (user != null){
                startActivity(MainActivity())
            } else {
                transition()
            }
        }, timeOut)



    }

    private fun transition() {
        val intent = Intent(this, FirstScreenActivity::class.java)
        val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            binding.appLogoImageView,
            binding.appLogoImageView.transitionName
        )
        startActivity(intent, options.toBundle())
    }

}