package com.example.foodelivery.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

// --- 1. IMPORT AUTH ---
import com.example.foodelivery.presentation.auth.login.LoginScreen
import com.example.foodelivery.presentation.auth.contract.LoginEffect
import com.example.foodelivery.presentation.auth.register.RegisterScreen
import com.example.foodelivery.presentation.auth.register.contract.RegisterEffect
// --- 2. IMPORT ADMIN (Tên hàm Composable khớp chính xác với file bạn gửi) ---
import com.example.foodelivery.presentation.admin.home.AdminDashboardScreen
import com.example.foodelivery.presentation.admin.food.list.AdminFoodListScreen
import com.example.foodelivery.presentation.admin.food.detail.AdminFoodDetailScreen
import com.example.foodelivery.presentation.admin.order.list.AdminOrderListScreen
import com.example.foodelivery.presentation.admin.category.list.AdminCategoryListScreen
import com.example.foodelivery.presentation.admin.category.add_edit.AddEditCategoryScreen


// --- 3. IMPORT CUSTOMER ---
import com.example.foodelivery.presentation.customer.home.CustomerHomeScreen
import com.example.foodelivery.presentation.customer.cart.CustomerCartScreen
import com.example.foodelivery.presentation.customer.food.detail.FoodDetailScreen
import com.example.foodelivery.presentation.customer.profile.CustomerProfileScreen
import com.example.foodelivery.presentation.customer.tracking.CustomerTrackingScreen
import com.example.foodelivery.presentation.customer.food.list.FoodListScreen
import com.example.foodelivery.presentation.customer.profile.editprofile.CustomerEditProfileScreen
// --- 4. IMPORT DRIVER ---
import com.example.foodelivery.presentation.driver.dashboard.DriverDashboardScreen
import com.example.foodelivery.presentation.driver.delivery.DriverDeliveryScreen
import com.example.foodelivery.presentation.driver.profile.DriverProfileScreen
import com.example.foodelivery.presentation.driver.profile.editprofile.DriverEditProfileScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Route.Graph.AUTH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(navController)
        adminGraph(navController)
        customerGraph(navController)
        driverGraph(navController)
    }
}

// =================================================================
// 1. AUTH GRAPH
// =================================================================
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = Route.Login.path, route = Route.Graph.AUTH) {

        composable(Route.Login.path) {
            // [FIX]: Khớp với LoginScreen.kt bạn gửi (dùng onNavigate)
            LoginScreen(
                onNavigation = { effect ->
                    when(effect) {
                        is LoginEffect.Navigation.ToCustomerHome -> {
                            navController.navigate(Route.Graph.CUSTOMER) { popUpTo(Route.Graph.AUTH) { inclusive = true } }
                        }
                        is LoginEffect.Navigation.ToDriverDashboard -> {
                            navController.navigate(Route.Graph.DRIVER) { popUpTo(Route.Graph.AUTH) { inclusive = true } }
                        }
                        is LoginEffect.Navigation.ToAdminDashboard -> {
                            navController.navigate(Route.Graph.ADMIN) { popUpTo(Route.Graph.AUTH) { inclusive = true } }
                        }
                        // [FIX]: Xử lý đầy đủ các nhánh (Exhaustive)
                        is LoginEffect.Navigation.ToRegister -> {
                            navController.navigate(Route.Register.path)
                        }
                        is LoginEffect.Navigation.ToForgotPassword -> {
                            navController.navigate(Route.ForgotPassword.path)
                        }
                    }
                }
            )
        }

        composable(Route.Register.path) {RegisterScreen(
            onNavigation = { effect ->
                when(effect) {
                    is RegisterEffect.Navigation.ToLogin -> {
                        // Đăng ký xong hoặc bấm Back -> Về Login
                        navController.popBackStack()
                    }
                    is RegisterEffect.Navigation.ToHome -> {
                        // Nếu muốn đăng ký xong vào luôn App
                        navController.navigate(Route.Graph.CUSTOMER) {
                            popUpTo(Route.Graph.AUTH) { inclusive = true }
                        }
                    }
                }
            }
        ) }
        composable(Route.ForgotPassword.path) { /* ForgotPasswordScreen */ }
    }
}

// =================================================================
// 2. ADMIN GRAPH (Khớp 100% với các file bạn upload)
// =================================================================
fun NavGraphBuilder.adminGraph(navController: NavHostController) {
    navigation(startDestination = Route.AdminDashboard.path, route = Route.Graph.ADMIN) {

        // 1. Dashboard: Nhận navController trực tiếp (Theo file AdminDashboardScreen.kt)
        composable(Route.AdminDashboard.path) {
            AdminDashboardScreen(navController = navController)
        }

        // 2. Food List: Nhận 3 callback (Theo file AdminFoodListScreen.kt)
        composable(Route.AdminFoodList.path) {
            AdminFoodListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Route.AddFood.path) },
                onNavigateToEdit = { foodId -> navController.navigate(Route.EditFood.createRoute(foodId)) }
            )
        }

        // 3. Category List: Nhận 3 callback (Theo file AdminCategoryListScreen.kt)
        composable(Route.AdminCategoryList.path) {
            AdminCategoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                // Giả định bạn đã thêm Route.AddCategory và Route.EditCategory vào file Route.kt
                // Nếu chưa, hãy dùng string tạm: "admin_add_category"
                onNavigateToAdd = { navController.navigate("admin_add_category") },
                onNavigateToEdit = { catId -> navController.navigate("admin_edit_category/$catId") }
            )
        }

        // 4. Order List: Nhận navController trực tiếp (Theo file AdminOrderListScreen.kt)
        composable(Route.AdminOrderList.path) {
            AdminOrderListScreen(navController = navController)
        }

        // 5. Food Detail (Add Mode) - (Theo file AdminFoodDetailScreen.kt)
        composable(Route.AddFood.path) {
            AdminFoodDetailScreen(
                onNavigateBack = { navController.popBackStack() }
                // Không truyền foodId -> ViewModel tự hiểu là Add
            )
        }

        // 6. Food Detail (Edit Mode)
        composable(
            route = Route.EditFood.path,
            arguments = Route.EditFood.navArgs
        ) {
            AdminFoodDetailScreen(
                onNavigateBack = { navController.popBackStack() }
                // ViewModel tự lấy ID từ SavedStateHandle
            )
        }

        // 7. Category Detail (Add Mode) - (Theo file AddEditCategoryScreen.kt)
        // Lưu ý: Đảm bảo Route.kt có route này hoặc dùng chuỗi string bên dưới
        composable("admin_add_category") {
            AddEditCategoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 8. Category Detail (Edit Mode)
        composable(
            route = "admin_edit_category/{categoryId}",
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) {
            AddEditCategoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// =================================================================
// 3. CUSTOMER GRAPH
// =================================================================
fun NavGraphBuilder.customerGraph(navController: NavHostController) {
    navigation(startDestination = Route.CustomerHome.path, route = Route.Graph.CUSTOMER) {

        // 1. HOME SCREEN
        composable(Route.CustomerHome.path) {
            CustomerHomeScreen(navController = navController)
        }

        // 2. FOOD LIST SCREEN (Xem tất cả / Danh mục)
        composable(
            route = Route.CustomerFoodList.path,
            arguments = Route.CustomerFoodList.navArgs
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString(Route.CustomerFoodList.ARG_TYPE) ?: "popular"
            FoodListScreen(navController = navController, type = type)
        }

        // 3. FOOD DETAIL SCREEN (Chi tiết món)
        composable(
            route = Route.CustomerFoodDetail.path,
            arguments = Route.CustomerFoodDetail.navArgs
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString(Route.CustomerFoodDetail.ARG_FOOD_ID) ?: ""
            FoodDetailScreen(navController = navController, foodId = foodId)
        }

        // 4. CART SCREEN
        composable(Route.CustomerCart.path) {
            CustomerCartScreen(navController = navController)
        }

        // 5. PROFILE SCREEN
        composable(Route.CustomerProfile.path) {
            CustomerProfileScreen(navController = navController)
        }

        // 6. TRACKING SCREEN
        composable(
            route = Route.CustomerTracking.path,
            arguments = Route.CustomerTracking.navArgs
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Route.CustomerTracking.ARG_ORDER_ID) ?: ""
            CustomerTrackingScreen(navController = navController, orderId = orderId)
        }

        // 7.EDIT PROFILE
        composable(Route.CustomerEditProfile.path) {
            CustomerEditProfileScreen(navController = navController)        }
    }
}

// =================================================================
// 4. DRIVER GRAPH
// =================================================================
fun NavGraphBuilder.driverGraph(navController: NavHostController) {
    navigation(startDestination = Route.DriverDashboard.path, route = Route.Graph.DRIVER) {
        composable(Route.DriverDashboard.path) { DriverDashboardScreen(navController) }

        composable(
            route = Route.DriverDelivery.path,
            arguments = Route.DriverDelivery.navArgs
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Route.DriverDelivery.ARG_ORDER_ID) ?: ""
            DriverDeliveryScreen(navController, orderId = orderId)
        }
    }

    composable(Route.DriverProfile.path) {
        DriverProfileScreen(navController = navController)
    }

    // 4. Edit Profile
    composable(Route.DriverEditProfile.path) {
        DriverEditProfileScreen(navController = navController)
    }
}