package com.example.foodelivery.presentation.admin.category.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.usecase.category.DeleteCategoryUseCase
import com.example.foodelivery.domain.usecase.category.GetCategoriesUseCase
import com.example.foodelivery.presentation.admin.category.list.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryListState())
    val state = _state.asStateFlow()

    private val _effect = Channel<CategoryListEffect>()
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadCategories()
    }

    fun processIntent(intent: CategoryListIntent) {
        when (intent) {
            is CategoryListIntent.SearchCategory -> {
                _state.update { it.copy(searchQuery = intent.query) }
                performSearch(intent.query)
            }
            CategoryListIntent.ClickAddCategory -> sendEffect(CategoryListEffect.NavigateToAddScreen)
            is CategoryListIntent.ClickEditCategory -> sendEffect(CategoryListEffect.NavigateToEditScreen(intent.id))
            is CategoryListIntent.ClickDeleteCategory -> {
                // Tìm item cần xóa để hiển thị lên dialog
                val item = _state.value.categories.find { it.id == intent.id }
                if (item != null) _state.update { it.copy(categoryToDelete = item) }
            }
        }
    }

    // --- DIALOG ACTIONS ---
    fun onConfirmDelete() {
        val item = _state.value.categoryToDelete ?: return
        deleteCategory(item.id)
        _state.update { it.copy(categoryToDelete = null) }
    }

    fun onDismissDelete() {
        _state.update { it.copy(categoryToDelete = null) }
    }

    // --- LOGIC ---
    private fun loadCategories() = performSearch("")

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isNotEmpty()) delay(300) // Debounce

            getCategoriesUseCase(query).collect { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> _state.update { it.copy(isLoading = false, categories = result.data ?: emptyList()) }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false) }
                        sendEffect(CategoryListEffect.ShowToast(result.message ?: "Lỗi"))
                    }
                }
            }
        }
    }

    private fun deleteCategory(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = deleteCategoryUseCase(id)) {
                is Resource.Success -> {
                    sendEffect(CategoryListEffect.ShowToast("Đã xóa thành công"))
                    loadCategories()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(CategoryListEffect.ShowToast(result.message ?: "Lỗi xóa"))
                }
                else -> {}
            }
        }
    }

    private fun sendEffect(effect: CategoryListEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}