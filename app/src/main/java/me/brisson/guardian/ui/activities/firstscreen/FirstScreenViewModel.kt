package me.brisson.guardian.ui.activities.firstscreen

import dagger.hilt.android.lifecycle.HiltViewModel
import me.brisson.guardian.data.repository.MainRepository
import me.brisson.guardian.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FirstScreenViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {




}