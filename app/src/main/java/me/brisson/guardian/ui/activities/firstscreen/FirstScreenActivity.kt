package me.brisson.guardian.ui.activities.firstscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.ActivityFirstScreenBinding
import me.brisson.guardian.ui.activities.main.MainActivity
import me.brisson.guardian.ui.base.BaseActivity
import me.brisson.guardian.ui.fragments.login.LoginFragment
import me.brisson.guardian.ui.fragments.signup.SignUpFragment
import me.brisson.guardian.utils.SocialsLogin


@AndroidEntryPoint
class FirstScreenActivity : BaseActivity() {

    lateinit var socialsLogin: SocialsLogin

    private lateinit var binding: ActivityFirstScreenBinding
    private val viewModel: FirstScreenViewModel by viewModels()

    private val login = LoginFragment.newInstance()
    private val signUp = SignUpFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_first_screen)
        binding.viewModel = viewModel

        initializeSocialsLogin()

        openFragment(login)

        setupUI()

    }

    private fun initializeSocialsLogin() {
        socialsLogin = SocialsLogin(this)

        // Google
        socialsLogin.initializeGoogleButton()

        //Facebook
        socialsLogin.initializeFacebookButton(binding.fbLoginButton)
        socialsLogin.registerFacebookCallback { isSuccessFull ->
            if (isSuccessFull) {
                startActivity(MainActivity())
            } else {
                Toast.makeText(
                    this,
                    "Ocorreu um erro no login com o Facebook",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Twitter
        socialsLogin.initializeTwitterButton()
    }

    private fun setupUI() {
        binding.googleImageView.setOnClickListener {
            socialsLogin.googleSignIn()
        }

        binding.facebookImageView.setOnClickListener {
            binding.fbLoginButton.performClick()
        }

        binding.twitterImageView.setOnClickListener {
            socialsLogin.signInWithProvider{
                if (it){
                    startActivity(MainActivity())
                }
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> openFragment(login, 1)
                    1 -> openFragment(signUp, 2)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handling google result
        socialsLogin.handleGoogleResult(requestCode, data) { isSuccessful ->
            if (isSuccessful) {
                startActivity(MainActivity())
            } else {
                Toast.makeText(
                    this,
                    "Ocorreu um erro no login com o Google",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Pass the activity result back to the Facebook SDK
        socialsLogin.handleFacebookResult(requestCode, resultCode, data)

        // Checking Twitter pending result
        socialsLogin.checkPendingResult()
    }

    private fun openFragment(fragment: Fragment, id: Int = 0) {
        val transaction = supportFragmentManager.beginTransaction()
        when (id) {
            1 -> transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
            2 -> transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onStart() {
        super.onStart()
        // Checking if are any user logged in
        if (
            Firebase.auth.currentUser != null &&
            GoogleSignIn.getLastSignedInAccount(this) != null
        ) {
            startActivity(MainActivity())
        }
    }

}