package me.brisson.guardian.ui.fragments.myprofile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import me.brisson.guardian.R
import me.brisson.guardian.data.model.User
import me.brisson.guardian.databinding.FragmentMyProfileBinding
import me.brisson.guardian.ui.activities.editprofile.EditProfileActivity
import me.brisson.guardian.ui.activities.firstscreen.FirstScreenActivity
import me.brisson.guardian.ui.activities.myguardians.MyGuardiansActivity
import me.brisson.guardian.ui.activities.notifications.NotificationsActivity
import me.brisson.guardian.ui.base.BaseFragment

//TODO() Update the UI when the fragment is back from EditProfile
@AndroidEntryPoint
class MyProfileFragment : BaseFragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private lateinit var binding: FragmentMyProfileBinding
    private var viewModel = MyProfileViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewModel.setUser()
        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user != null){
                setupUI(user)
            }
        }

        handleClickListeners()

        return binding.root
    }

    private fun setupUI(user: User) {
        viewModel.name.value = user.name
        viewModel.email.value = user.email
        viewModel.photo.value = user.userImage

        if (!viewModel.photo.value.isNullOrEmpty()){
            Picasso.get()
                .load(user.userImage)
                .resize(300, 300)
                .centerCrop()
                .into(binding.userImageView)
        }

        binding.let {
            it.userNameTextView.text = user.name
            it.userEmailTextView.text = user.email
        }

    }

    private fun handleClickListeners() {
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
            viewModel.logout()
            startActivity(
                FirstScreenActivity(),
                flag = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    override fun onResume() {
        viewModel.setUser()
        super.onResume()
    }

}