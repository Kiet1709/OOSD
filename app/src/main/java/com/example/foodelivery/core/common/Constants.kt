package com.example.foodelivery.core.common

object Constants {
    // =================================================================
    // 1. FIRESTORE COLLECTIONS (Khớp 100% với ảnh chụp CSDL)
    // =================================================================
    const val COLL_USERS = "users"
    const val COLL_FOODS = "foods"
    const val COLL_CATEGORIES = "categories"       // Danh mục món ăn
    const val COLL_ORDERS = "orders"               // Đơn hàng
    const val COLL_REVIEWS = "reviews"             // Đánh giá
    const val COLL_DRIVER_LOCATIONS = "driver_locations" // Vị trí tài xế (Lưu ý: trong ảnh là collection)

    // =================================================================
    // 2. FIREBASE STORAGE PATHS (Thư mục lưu ảnh)
    // =================================================================
    const val PATH_FOOD_IMAGES = "food_images"
    const val PATH_AVATARS = "avatars"
    const val PATH_CATEGORY_IMAGES = "category_images"

    // =================================================================
    // 3. BUSINESS LOGIC CONSTANTS (Tránh Hardcode string)
    // =================================================================

    // --- USER ROLES ---
    const val ROLE_CUSTOMER = "CUSTOMER"
    const val ROLE_DRIVER = "DRIVER"
    const val ROLE_ADMIN = "ADMIN"

    // --- ORDER STATUS (Quy trình đơn hàng) ---
    const val STATUS_PENDING = "PENDING"       // Chờ xác nhận
    const val STATUS_CONFIRMED = "CONFIRMED"   // Nhà hàng đã nhận, đang làm
    const val STATUS_DELIVERING = "DELIVERING" // Tài xế đang giao
    const val STATUS_COMPLETED = "COMPLETED"   // Giao thành công
    const val STATUS_CANCELLED = "CANCELLED"   // Đã hủy

    // =================================================================
    // 4. LOCAL STORAGE (Room & DataStore)
    // =================================================================
    const val DB_NAME = "food_delivery.db"
    const val PREFS_NAME = "food_app_prefs"

    // Keys lưu trong DataStore/SharedPreferences
    const val KEY_AUTH_TOKEN = "auth_token"
    const val KEY_USER_INFO = "user_info_json"
    const val KEY_IS_ONBOARDING_SHOWN = "is_onboarding_shown"

    // =================================================================
    // 5. CONFIGURATION
    // =================================================================
    const val NETWORK_TIMEOUT = 30000L // 30 giây
    const val LOCATION_UPDATE_INTERVAL = 5000L // 5 giây cập nhật vị trí 1 lần

    // Tin nhắn lỗi mặc định
    const val MSG_UNKNOWN_ERROR = "Đã xảy ra lỗi không xác định"
    const val MSG_NETWORK_ERROR = "Vui lòng kiểm tra kết nối mạng"
}