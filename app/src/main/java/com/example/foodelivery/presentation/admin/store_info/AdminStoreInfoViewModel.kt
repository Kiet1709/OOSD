package com.example.foodelivery.presentation.admin.store_info

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.StoreInfo
import com.example.foodelivery.domain.repository.IStoreRepository
import com.example.foodelivery.presentation.admin.store_info.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminStoreInfoViewModel @Inject constructor(
    private val repository: IStoreRepository
) : BaseViewModel<StoreInfoState, StoreInfoIntent, StoreInfoEffect>(StoreInfoState()) {

    init {
        handleIntent(StoreInfoIntent.LoadData)
    }

    override fun handleIntent(intent: StoreInfoIntent) {
        when(intent) {
            StoreInfoIntent.LoadData -> loadData()
            is StoreInfoIntent.UpdateName -> setState { copy(name = intent.value) }
            is StoreInfoIntent.UpdateAddress -> setState { copy(address = intent.value) }
            is StoreInfoIntent.UpdatePhone -> setState { copy(phone = intent.value) }
            is StoreInfoIntent.UpdateDescription -> setState { copy(description = intent.value) }
            is StoreInfoIntent.UpdateAvatar -> setState { copy(avatarUrl = intent.value) }
            is StoreInfoIntent.UpdateCover -> setState { copy(coverUrl = intent.value) }
            StoreInfoIntent.Save -> saveData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            repository.getStoreInfo().collectLatest { result ->
                when(result) {
                    is Resource.Success -> {
                        val info = result.data
                        setState {
                            copy(
                                isLoading = false,
                                info = info,
                                name = info?.name ?: "",
                                address = info?.address ?: "",
                                phone = info?.phoneNumber ?: "",
                                description = info?.description ?: "",
                                avatarUrl = info?.avatarUrl ?: "",
                                coverUrl = info?.coverUrl ?: ""
                            )
                        }
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { StoreInfoEffect.ShowToast(result.message ?: "Lỗi tải thông tin") }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun saveData() {
        val s = currentState
        if (s.name.isBlank()) {
            setEffect { StoreInfoEffect.ShowToast("Tên quán không được để trống") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val newInfo = StoreInfo(
                name = s.name,
                address = s.address,
                phoneNumber = s.phone,
                description = s.description,
                avatarUrl = s.avatarUrl,
                coverUrl = s.coverUrl
            )
            
            val result = repository.updateStoreInfo(newInfo)
            setState { copy(isLoading = false) }
            
            when(result) {
                is Resource.Success -> {
                    setEffect { StoreInfoEffect.ShowToast("Lưu thành công!") }
                    setEffect { StoreInfoEffect.NavigateBack }
                }
                is Resource.Error -> {
                    setEffect { StoreInfoEffect.ShowToast(result.message ?: "Lỗi lưu") }
                }
                else -> {}
            }
        }
    }
}