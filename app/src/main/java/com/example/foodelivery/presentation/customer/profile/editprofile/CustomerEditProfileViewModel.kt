package com.example.foodelivery.presentation.customer.profile.editprofile

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.profile.editprofile.contract.*
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerEditProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : BaseViewModel<EditProfileState, EditProfileIntent, EditProfileEffect>(EditProfileState()) {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    init {
        handleIntent(EditProfileIntent.LoadData)
    }

    fun setEvent(intent: EditProfileIntent) = handleIntent(intent)

    override fun handleIntent(intent: EditProfileIntent) {
        when(intent) {
            EditProfileIntent.LoadData -> loadUserProfile()

            is EditProfileIntent.ChangeName -> setState { copy(name = intent.value) }
            is EditProfileIntent.ChangePhone -> setState { copy(phone = intent.value) }
            is EditProfileIntent.ChangeAddress -> setState { copy(address = intent.value) }
            is EditProfileIntent.ChangeAvatar -> setState { copy(avatarUrl = intent.value) }
            is EditProfileIntent.ChangeCoverPhoto -> setState { copy(coverPhotoUrl = intent.value) }

            EditProfileIntent.ClickBack -> setEffect { EditProfileEffect.GoBack }

            EditProfileIntent.ClickSave -> saveUserProfile()
        }
    }

    private fun loadUserProfile() {
        if (uid == null) return
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            when(val result = userRepository.getUser(uid)) {
                is Resource.Success<*> -> {
                    val user = result.data as? com.example.foodelivery.domain.model.User

                    if (user != null) {
                        setState {
                            copy(
                                isLoading = false,
                                name = user.name,
                                phone = user.phoneNumber,
                                address = user.address ?: "",
                                avatarUrl = user.avatarUrl,
                                coverPhotoUrl = user.coverPhotoUrl
                            )
                        }
                    } else {
                        setState { copy(isLoading = false) }
                    }
                }

                is Resource.Error<*> -> {
                    setState { copy(isLoading = false) }
                    setEffect { EditProfileEffect.ShowToast(result.message ?: "Lỗi tải dữ liệu") }
                }

                else -> {}
            }
        }
    }

    private fun saveUserProfile() {
        if (uid == null) return
        if (currentState.name.isBlank()) {
            setEffect { EditProfileEffect.ShowToast("Tên không được để trống") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val result = userRepository.updateProfile(
                uid = uid,
                name = currentState.name,
                phone = currentState.phone,
                address = currentState.address,
                avatarUrl = currentState.avatarUrl,
                coverPhotoUrl = currentState.coverPhotoUrl
            )

            setState { copy(isLoading = false) }

            when(result) {
                is Resource.Success<*> -> {
                    setEffect { EditProfileEffect.ShowToast("Đã cập nhật hồ sơ") }
                    setEffect { EditProfileEffect.GoBack }
                }
                is Resource.Error<*> -> {
                    setEffect { EditProfileEffect.ShowToast(result.message ?: "Lỗi cập nhật") }
                }
                else -> {}
            }
        }
    }
}