package com.example.foodelivery.ui.theme.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Route(val path: String) {

    object Graph {
        const val AUTH = "auth_graph"
        const val ADMIN = "admin_graph"
        const val RESTAURANT = "restaurant_graph"
        const val CUSTOMER = "customer_graph"
        const val DRIVER = "driver_graph"
    }

    // --- AUTH ---
    object Login : Route("login")
    object Register : Route("register")
    object ForgotPassword : Route("forgot_password")

    // --- ADMIN ---
    object AdminDashboard : Route("admin_dashboard")
    object AdminCategoryList : Route("admin_category_list")

    // --- RESTAURANT ---
    object RestaurantDashboard : Route("restaurant_dashboard")
    object RestaurantFoodList : Route("restaurant_food_list")
    object RestaurantProfile : Route("restaurant_profile") // View Profile
    object RestaurantEditProfile : Route("restaurant_edit_profile") // Edit Profile
    object RestaurantAddEditFood : Route("restaurant_add_edit_food/{foodId}") {
        const val ARG_FOOD_ID = "foodId"
        fun createRoute(foodId: String) = "restaurant_add_edit_food/$foodId"
        val navArgs = listOf(navArgument(ARG_FOOD_ID) { type = NavType.StringType })
    }

    // --- CUSTOMER ---
    object CustomerHome : Route("customer_home")
    object CustomerCart : Route("customer_cart")
    object CustomerProfile : Route("customer_profile")
    object CustomerEditProfile : Route("customer_edit_profile")
    object CustomerAddress : Route("customer_address")
    object CustomerOrderHistory : Route("customer_order_history")
    object CustomerSettings : Route("customer_settings")
    object Checkout : Route("checkout") // Add this
    object CustomerFoodDetail : Route("customer_food_detail/{foodId}"){
        const val ARG_FOOD_ID = "foodId"
        fun createRoute(foodId: String) = "customer_food_detail/$foodId"
        val navArgs = listOf(navArgument(ARG_FOOD_ID) { type = NavType.StringType })
    }
    object CustomerFoodList : Route("customer_food_list/{type}") {
        const val ARG_TYPE = "type"
        fun createRoute(type: String) = "customer_food_list/$type"
        val navArgs = listOf(navArgument(ARG_TYPE) { type = NavType.StringType })
    }
    object CustomerTracking : Route("customer_tracking/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String) = "customer_tracking/$orderId"
        val navArgs = listOf(navArgument(ARG_ORDER_ID) { type = NavType.StringType })
    }

    // --- DRIVER ---
    object DriverDashboard : Route("driver_dashboard")
    object DriverProfile : Route("driver_profile")
    object DriverEditProfile : Route("driver_edit_profile")
    object DriverDelivery : Route("driver_delivery/{orderId}") {
        const val ARG_ORDER_ID = "orderId"
        fun createRoute(orderId: String) = "driver_delivery/$orderId"
        val navArgs = listOf(navArgument(ARG_ORDER_ID) { type = NavType.StringType })
    }
}