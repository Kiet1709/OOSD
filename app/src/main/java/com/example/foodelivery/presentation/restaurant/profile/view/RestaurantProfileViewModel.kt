package com.example.foodelivery.presentation.restaurant.profile.view

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.restaurant.profile.view.contract.RestaurantProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : BaseViewModel<RestaurantProfileState, Nothing, Nothing>(RestaurantProfileState()) {

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            userRepository.getUser().collect { user ->
                setState { copy(isLoading = false, user = user) }
            }
        }
    }

    override fun handleIntent(intent: Nothing) {}
}