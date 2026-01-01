package com.example.foodelivery.core.common

import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.presentation.customer.home.contract.CategoryUiModel
import com.example.foodelivery.presentation.customer.home.contract.FoodUiModel

/**
 * Đóng vai trò là một "Fake Database" (In-memory).
 * Chứa dữ liệu có thể Thêm/Sửa/Xóa ngay lập tức để Demo.
 */
object MockData {

    // --- 1. DANH MỤC (Fake Table Categories) ---
    val categories = mutableListOf(
        CategoryUiModel("1", "Burger", "https://cdn-icons-png.flaticon.com/512/3075/3075977.png"),
        CategoryUiModel("2", "Pizza", "https://cdn-icons-png.flaticon.com/512/1404/1404945.png"),
        CategoryUiModel("3", "Cơm", "https://cdn-icons-png.flaticon.com/512/261/261444.png"),
        CategoryUiModel("4", "Đồ uống", "https://cdn-icons-png.flaticon.com/512/2405/2405597.png")
    )

    // --- 2. MÓN ĂN (Fake Table Foods) ---
    // Sử dụng MutableList để hỗ trợ thêm/sửa/xóa khi Demo
    private val _foodList = mutableListOf(
        Food(
            id = "f1",
            name = "Burger Bò Phô Mai",
            description = "Burger bò nướng lửa hồng, kèm phô mai Cheddar tan chảy và rau tươi.",
            price = 55000.0,
            imageUrl = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
            categoryId = "1",
            rating = 4.8,
            isAvailable = true
        ),
        Food(
            id = "f2",
            name = "Pizza Hải Sản",
            description = "Pizza đế mỏng giòn rụm với tôm, mực và sốt cà chua đặc biệt.",
            price = 120000.0,
            imageUrl = "https://images.unsplash.com/photo-1513104890138-7c749659a591",
            categoryId = "2",
            rating = 4.5,
            isAvailable = true
        ),
        Food(
            id = "f3",
            name = "Cá hồi áp chảo",
            description = "Cá hổ áp chảo sốt cam.",
            price = 45000.0,
            imageUrl = "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2",
            categoryId = "3",
            rating = 4.9,
            isAvailable = true
        )
    )

    // Expose list ra ngoài (Read-only) để an toàn
    val foodList: List<Food> get() = _foodList

    // --- CÁC HÀM CRUD GIẢ LẬP (Mô phỏng hành vi Database) ---

    fun getFoodById(id: String): Food? {
        return _foodList.find { it.id == id }
    }

    fun addFood(food: Food) {
        // Giả lập tạo ID nếu chưa có
        val newFood = if (food.id.isEmpty()) food.copy(id = "f${System.currentTimeMillis()}") else food
        _foodList.add(newFood)
    }

    fun updateFood(food: Food) {
        val index = _foodList.indexOfFirst { it.id == food.id }
        if (index != -1) {
            _foodList[index] = food
        }
    }

    fun deleteFood(id: String) {
        _foodList.removeAll { it.id == id }
    }
}