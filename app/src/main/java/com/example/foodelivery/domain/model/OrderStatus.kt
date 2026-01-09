package com.example.foodelivery.domain.model

enum class OrderStatus(val value: String, val title: String) {
    // Định nghĩa cả Value (cho Database) và Title (cho UI hiển thị)
    NEW("NEW", "Mới"),
    PENDING("PENDING", "Chờ xử lý"),
    CONFIRMED("CONFIRMED", "Đã xác nhận"),
    PREPARING("PREPARING", "Đang làm"),
    DELIVERING("DELIVERING", "Đang giao"),
    DELIVERED("DELIVERED", "Đã giao"),

    COMPLETED("COMPLETED", "Hoàn thành"),
    HISTORY("HISTORY", "Lịch sử"), // Dùng cho Tab Lịch sử
    CANCELLED("CANCELLED", "Đã hủy");

    companion object {
        // Hàm này giúp convert từ String trong Database thành Enum
        fun fromString(value: String?): OrderStatus = entries.find { it.value == value } ?: NEW

        fun isFinalStatus(status: OrderStatus): Boolean {
            return status == DELIVERED || status == CANCELLED
        }
    }
}