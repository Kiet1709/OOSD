package com.example.foodelivery.presentation.admin.food.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.usecase.food.DeleteFoodUseCase
import com.example.foodelivery.domain.usecase.food.GetFoodsUseCase
import com.example.foodelivery.domain.usecase.food.ToggleFoodStatusUseCase
import com.example.foodelivery.presentation.admin.food.list.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    private val toggleStatusUseCase: ToggleFoodStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FoodListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<FoodListEffect>()
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadData()
    }

    fun processIntent(intent: FoodListIntent) {
        when (intent) {
            is FoodListIntent.SearchFood -> {
                _state.update { it.copy(searchQuery = intent.query) }
                performSearch(intent.query)
            }
            FoodListIntent.ClickAddFood -> sendEffect(FoodListEffect.NavigateToAddScreen)
            is FoodListIntent.ClickEditFood -> sendEffect(FoodListEffect.NavigateToEditScreen(intent.id))
            is FoodListIntent.ClickDeleteFood -> deleteFood(intent.id)
            is FoodListIntent.ToggleAvailability -> toggleStatus(intent.id)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getFoodsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Map Domain -> UI Model
                        val items = result.data?.map {
                            FoodUiModel(it.id, it.name, it.price, it.imageUrl, it.isAvailable)
                        } ?: emptyList()

                        _state.update {
                            it.copy(isLoading = false, foodList = items, displayedList = items)
                        }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(FoodListEffect.ShowToast(result.message ?: "Lỗi tải dữ liệu"))
                    }
                    else -> {}
                }
            }
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            val currentList = _state.value.foodList
            val filtered = if (query.isBlank()) currentList else {
                currentList.filter { it.name.contains(query, ignoreCase = true) }
            }
            _state.update { it.copy(displayedList = filtered) }
        }
    }

    private fun deleteFood(id: String) {
        viewModelScope.launch {
            val result = deleteFoodUseCase(id)
            if (result is Resource.Success) {
                sendEffect(FoodListEffect.ShowToast("Đã xóa thành công"))
                loadData() // Refresh list
            } else {
                sendEffect(FoodListEffect.ShowToast(result.message ?: "Xóa thất bại"))
            }
        }
    }

    private fun toggleStatus(id: String) {
        viewModelScope.launch {
            // Optimistic Update: Cập nhật UI trước cho mượt
            val updatedList = _state.value.displayedList.map {
                if (it.id == id) it.copy(isAvailable = !it.isAvailable) else it
            }
            _state.update { it.copy(displayedList = updatedList) }

            // Gọi API sau
            val item = _state.value.foodList.find { it.id == id } ?: return@launch
            val result = toggleStatusUseCase(id, !item.isAvailable)

            if (result is Resource.Error) {
                // Revert nếu lỗi
                loadData()
                sendEffect(FoodListEffect.ShowToast("Lỗi cập nhật trạng thái"))
            }
        }
    }

    private fun sendEffect(effect: FoodListEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}