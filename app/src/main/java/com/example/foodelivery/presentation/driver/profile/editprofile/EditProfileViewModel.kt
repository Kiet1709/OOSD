package com.example.foodelivery.presentation.driver.profile.editprofile

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IUserRepository // <--- QUAN TRỌNG: Dùng cái này
import com.example.foodelivery.presentation.driver.profile.editprofile.contract.* // Import Contract của Driver
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverEditProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : BaseViewModel<DriverEditProfileState, DriverEditProfileIntent, DriverEditProfileEffect>(DriverEditProfileState()) {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    init {
        handleIntent(DriverEditProfileIntent.LoadData)
    }

    fun setEvent(intent: DriverEditProfileIntent) = handleIntent(intent)

    override fun handleIntent(intent: DriverEditProfileIntent) {
        when(intent) {
            DriverEditProfileIntent.LoadData -> loadUserProfile()

            // Các hành động thay đổi text
            is DriverEditProfileIntent.ChangeName -> setState { copy(name = intent.value) }
            is DriverEditProfileIntent.ChangePhone -> setState { copy(phone = intent.value) }
            is DriverEditProfileIntent.ChangeAddress -> setState { copy(address = intent.value) }

            DriverEditProfileIntent.ClickBack -> setEffect { DriverEditProfileEffect.GoBack }
            DriverEditProfileIntent.ClickSave -> saveUserProfile()
        }
    }

    // Hàm load dữ liệu từ Firebase (bảng Users)
    private fun loadUserProfile() {
        if (uid == null) return
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // Gọi hàm getUser quen thuộc
            when(val result = userRepository.getUser(uid)) {
                is Resource.Success<*> -> {
                    // Ép kiểu dữ liệu về User model
                    val user = result.data as? com.example.foodelivery.domain.model.User

                    if (user != null) {
                        setState {
                            copy(
                                isLoading = false,
                                name = user.name,
                                phone = user.phoneNumber,
                                address = user.address ?: "",
                                avatarUrl = user.avatarUrl
                            )
                        }
                    } else {
                        // Trường hợp không tìm thấy user hoặc null
                        setState { copy(isLoading = false) }
                    }
                }
                is Resource.Error<*> -> {
                    setState { copy(isLoading = false) }
                    setEffect { DriverEditProfileEffect.ShowToast(result.message ?: "Lỗi tải dữ liệu") }
                }
                else -> {}
            }
        }
    }

    // Hàm lưu dữ liệu vào Firebase (bảng Users)
    private fun saveUserProfile() {
        if (uid == null) return

        // Validate cơ bản
        if (currentState.name.isBlank()) {
            setEffect { DriverEditProfileEffect.ShowToast("Tên không được để trống") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // Gọi hàm updateProfile của User Repo
            val result = userRepository.updateProfile(
                uid = uid,
                name = currentState.name,
                phone = currentState.phone,
                address = currentState.address
            )

            setState { copy(isLoading = false) }

            when(result) {
                is Resource.Success<*> -> {
                    setEffect { DriverEditProfileEffect.ShowToast("Cập nhật hồ sơ thành công") }
                    setEffect { DriverEditProfileEffect.GoBack }
                }
                is Resource.Error<*> -> {
                    setEffect { DriverEditProfileEffect.ShowToast(result.message ?: "Lỗi cập nhật") }
                }
                else -> {}
            }
        }
    }
}