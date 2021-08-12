package me.brisson.guardian.ui.fragments.myprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.databinding.FragmentMyProfileBinding
import me.brisson.guardian.ui.activities.editprofile.EditProfileActivity
import me.brisson.guardian.ui.activities.firstscreen.FirstScreenActivity
import me.brisson.guardian.ui.activities.myguardians.MyGuardiansActivity
import me.brisson.guardian.ui.activities.notifications.NotificationsActivity
import me.brisson.guardian.ui.base.BaseFragment

@AndroidEntryPoint
class MyProfileFragment : BaseFragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private lateinit var binding : FragmentMyProfileBinding
    private var viewModel = MyProfileViewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        setupUI(Firebase.auth.currentUser)
        handleClickListeners()

        return binding.root
    }

    private fun setupUI(user: FirebaseUser?){
        if (user != null){
            viewModel.name.value = user.displayName
            viewModel.email.value = user.email

            if (user.photoUrl != null){
                Picasso.get()
                    .load(user.photoUrl)
                    .resize(1000, 1000)
                    .centerCrop()
                    .into(binding.userImageView)
            }

            binding.let {
                it.userNameTextView.text = user.displayName
                it.userEmailTextView.text = user.email
            }

        }
    }

    private fun handleClickListeners(){
        binding.editProfileLayout.setOnClickListener {
            startActivity(EditProfileActivity())
            requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.stay_put)
        }

        binding.myGuardiansLayout.setOnClickListener {
            startActivity(MyGuardiansActivity())
            requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.stay_put)
        }

        binding.notificationsLayout.setOnClickListener {
            startActivity(NotificationsActivity())
            requireActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.stay_put)
        }

        binding.logoutLayout.setOnClickListener {
            startActivity(FirstScreenActivity())
            Firebase.auth.signOut()
        }
    }

    override fun onResume() {
        super.onResume()
        setupUI(Firebase.auth.currentUser)
    }


}