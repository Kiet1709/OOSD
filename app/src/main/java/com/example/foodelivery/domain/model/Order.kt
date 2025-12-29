package com.example.foodelivery.domain.model

data class Order(
    val id: String,
    val userId: String,
    val driverId: String?,
    val status: OrderStatus,
    val totalPrice: Double,
    val shippingAddress: String, // Chuẩn hóa
    val timestamp: Long,         // Dùng Long cho dễ xử lý
    val items: List<CartItem>
) {
//    // Logic Senior: Computed Properties (Tính toán trực tiếp trong Model)
//
//    // Format ngày tháng hiển thị (VD: "12:30 - 20/10/2023")
//    fun getFormattedDate(): String {
//        val date = java.util.Date(createdAt)
//        val format = java.text.SimpleDateFormat("HH:mm - dd/MM/yyyy", java.util.Locale.getDefault())
//        return format.format(date)
//    }
//
//    // Kiểm tra đơn hàng có cho phép hủy không? (Chỉ hủy khi còn PENDING)
//    fun canCancel(): Boolean {
//        return status == OrderStatus.PENDING
//    }

}