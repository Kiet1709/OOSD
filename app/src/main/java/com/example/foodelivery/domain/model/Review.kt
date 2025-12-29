package com.example.foodelivery.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Review(
    val id: String,
    val orderId: String,    // Đánh giá cho đơn hàng nào
    val foodId: String,     // Đánh giá món nào (Quan trọng để hiện ở chi tiết món)
    val userId: String,     // Ai đánh giá
    val userName: String,   // Lưu cứng tên người đánh giá (để đỡ phải query lại bảng User)
    val userAvatar: String, // Lưu cứng avatar
    val rating: Double,     // 1.0 đến 5.0
    val comment: String,
    val timestamp: Long
) {
    // --- HELPER CHO UI ---

    // Format ngày đánh giá (VD: 12 Th05, 2024)
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    // Kiểm tra xem có comment không (để ẩn dòng text nếu rỗng)
    val hasComment: Boolean
        get() = comment.isNotBlank()
}