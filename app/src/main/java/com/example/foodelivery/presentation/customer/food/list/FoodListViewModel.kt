package com.example.foodelivery.presentation.customer.food.list

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.presentation.customer.food.list.contract.*
import com.example.foodelivery.presentation.customer.home.contract.FoodUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(
    private val foodRepository: IFoodRepository
) : BaseViewModel<FoodListState, FoodListIntent, FoodListEffect>(FoodListState()) {

    // Hàm public để Screen gọi
    fun setEvent(intent: FoodListIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: FoodListIntent) {
        when(intent) {
            is FoodListIntent.LoadList -> loadFoods(intent.type)
            is FoodListIntent.ClickFood -> setEffect { FoodListEffect.NavigateToDetail(intent.id) }
            FoodListIntent.ClickBack -> setEffect { FoodListEffect.NavigateBack }
        }
    }

    private fun loadFoods(type: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                // [SENIOR LOGIC]: Xác định tiêu đề dựa trên type
                val title = when(type) {
                    "popular" -> "Món Ngon Nổi Bật"
                    "recommended" -> "Gợi Ý Cho Bạn"
                    else -> "Danh Sách Món Ăn" // Có thể query tên Category nếu cần
                }

                // Gọi Repository lấy dữ liệu thật
                val result = foodRepository.getFoodsByType(type)

                // Map Domain -> UI Model
                val uiFoods = result.map { food ->
                    FoodUiModel(
                        id = food.id,
                        name = food.name,
                        imageUrl = food.imageUrl,
                        price = food.price,
                        rating = 4.5, // Có thể update sau nếu API trả về rating
                        time = "20 min"
                    )
                }

                setState {
                    copy(isLoading = false, title = title, foods = uiFoods)
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect { FoodListEffect.ShowToast("Lỗi tải danh sách: ${e.message}") }
            }
        }
    }
}