package com.example.foodelivery.presentation.admin.food

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.example.foodelivery.domain.repository.IFoodRepository
import com.example.foodelivery.domain.usecase.food.AddFoodUseCase
import com.example.foodelivery.domain.usecase.food.DeleteFoodUseCase
import com.example.foodelivery.domain.usecase.food.GetMenuUseCase
import com.example.foodelivery.presentation.admin.food.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminFoodViewModel @Inject constructor(
    // Inject UseCase thay vì Repository
    private val getMenuUseCase: GetMenuUseCase,
    private val addFoodUseCase: AddFoodUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    
    // Vẫn giữ Repository cho các hàm chưa có UseCase (hoặc tạo thêm UseCase nếu cần triệt để)
    private val foodRepository: IFoodRepository, 
    private val categoryRepository: ICategoryRepository
) : BaseViewModel<AdminFoodState, AdminFoodIntent, AdminFoodEffect>(AdminFoodState()) {

    init {
        handleIntent(AdminFoodIntent.LoadData)
    }

    override fun handleIntent(intent: AdminFoodIntent) {
        when(intent) {
            AdminFoodIntent.LoadData -> loadData()
            is AdminFoodIntent.DeleteFood -> deleteFood(intent.id)
            is AdminFoodIntent.LoadFoodDetail -> loadDetail(intent.id)
            is AdminFoodIntent.SaveFood -> saveFood(intent)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            // Dùng UseCase để lấy menu
            combine(
                getMenuUseCase(),
                categoryRepository.getCategories()
            ) { foodRes, catRes ->
                val foods = foodRes.data ?: emptyList()
                val cats = catRes.data ?: emptyList()
                Pair(foods, cats)
            }.collect { (foods, cats) ->
                setState { copy(isLoading = false, foods = foods, categories = cats) }
            }
        }
    }

    private fun loadDetail(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Có thể tạo GetFoodDetailUseCase sau
            val result = foodRepository.getFoodDetail(id)
            if (result is Resource.Success) {
                setState { copy(isLoading = false, currentFood = result.data) }
            } else {
                setState { copy(isLoading = false, error = result.message) }
                setEffect { AdminFoodEffect.ShowToast(result.message ?: "Lỗi tải chi tiết") }
            }
        }
    }

    private fun deleteFood(id: String) {
        viewModelScope.launch {
            // Dùng UseCase
            val result = deleteFoodUseCase(id)
            if (result is Resource.Success) {
                setEffect { AdminFoodEffect.ShowToast("Đã xóa món ăn") }
            } else {
                setEffect { AdminFoodEffect.ShowToast("Xóa thất bại: ${result.message}") }
            }
        }
    }

    private fun saveFood(intent: AdminFoodIntent.SaveFood) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            val newFood = Food(
                id = intent.id ?: "f${System.currentTimeMillis()}",
                name = intent.name,
                description = intent.desc,
                price = intent.price,
                imageUrl = intent.imageUrl.ifBlank { "https://via.placeholder.com/150" },
                categoryId = intent.categoryId,
                rating = 5.0,
                isAvailable = true
            )

            // Dùng UseCase cho Add (Update vẫn dùng repo tạm thời)
            val result = if (intent.id == null) {
                addFoodUseCase(newFood)
            } else {
                foodRepository.updateFood(newFood)
            }

            setState { copy(isLoading = false) }

            if (result is Resource.Success) {
                setEffect { AdminFoodEffect.ShowToast("Lưu thành công") }
                setEffect { AdminFoodEffect.NavigateBack }
            } else {
                setEffect { AdminFoodEffect.ShowToast("Lỗi: ${result.message}") }
            }
        }
    }
}