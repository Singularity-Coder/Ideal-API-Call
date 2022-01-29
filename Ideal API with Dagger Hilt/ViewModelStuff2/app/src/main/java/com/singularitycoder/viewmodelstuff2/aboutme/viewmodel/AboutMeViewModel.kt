package com.singularitycoder.viewmodelstuff2.aboutme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.singularitycoder.viewmodelstuff2.aboutme.model.GitHubProfileQueryModel
import com.singularitycoder.viewmodelstuff2.aboutme.repository.AboutMeRepository
import com.singularitycoder.viewmodelstuff2.utils.network.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutMeViewModel @Inject constructor(
    application: Application,
    private val repository: AboutMeRepository,
) : AndroidViewModel(application) {

    private val aboutMe = repository.aboutMe

    internal fun getAboutMe(): LiveData<ApiState<GitHubProfileQueryModel?>> = aboutMe

    internal fun loadAboutMe() = viewModelScope.launch {
        repository.getAboutMe()
    }
}