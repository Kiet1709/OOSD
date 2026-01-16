package com.example.foodelivery.presentation.driver.delivery.contract

enum class DeliveryStep(val buttonText: String, val instruction: String) {
    HEADING_TO_RESTAURANT("ĐÃ ĐẾN QUÁN", "Di chuyển đến nhà hàng"),
    PICKING_UP("ĐÃ LẤY MÓN", "Nhận món và kiểm tra"),
    DELIVERING("ĐÃ ĐẾN NƠI", "Di chuyển đến khách hàng"),
    ARRIVED("HOÀN TẤT ĐƠN HÀNG", "Giao tận tay khách")
}