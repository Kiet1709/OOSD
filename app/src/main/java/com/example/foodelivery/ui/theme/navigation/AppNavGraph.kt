package com.example.foodelivery.ui.theme.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

// IMPORTS
import com.example.foodelivery.presentation.auth.login.LoginScreen
import com.example.foodelivery.presentation.auth.contract.LoginEffect
import com.example.foodelivery.presentation.auth.register.RegisterScreen
import com.example.foodelivery.presentation.auth.register.contract.RegisterEffect
import com.example.foodelivery.presentation.admin.home.AdminDashboardScreen
import com.example.foodelivery.presentation.admin.food.list.AdminFoodListScreen
import com.example.foodelivery.presentation.admin.food.detail.AdminFoodDetailScreen
import com.example.foodelivery.presentation.admin.order.list.AdminOrderListScreen
import com.example.foodelivery.presentation.admin.category.list.AdminCategoryListScreen
import com.example.foodelivery.presentation.admin.category.add_edit.AddEditCategoryScreen
import com.example.foodelivery.presentation.admin.store_info.AdminStoreInfoScreen
import com.example.foodelivery.presentation.customer.home.CustomerHomeScreen
import com.example.foodelivery.presentation.customer.cart.CustomerCartScreen
import com.example.foodelivery.presentation.customer.food.detail.FoodDetailScreen
import com.example.foodelivery.presentation.customer.profile.CustomerProfileScreen
import com.example.foodelivery.presentation.customer.tracking.CustomerTrackingScreen
import com.example.foodelivery.presentation.customer.food.list.FoodListScreen
import com.example.foodelivery.presentation.driver.dashboard.DriverDashboardScreen
import com.example.foodelivery.presentation.driver.delivery.DriverDeliveryScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Route.Graph.AUTH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -300 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        authGraph(navController)
        adminGraph(navController)
        customerGraph(navController)
        driverGraph(navController)
    }
}

// 1. AUTH GRAPH
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = Route.Login.path, route = Route.Graph.AUTH) {

        composable(Route.Login.path) {
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
                        is LoginEffect.Navigation.ToRegister -> {
                            // [FIX]: Sử dụng createRoute để điền tham số role vào URL
                            // Route.Register.path hiện tại là "register/{role}"
                            // Cần thay thế "{role}" bằng giá trị thực tế.
                            val route = Route.Register.createRoute(effect.preSelectedRole)
                            navController.navigate(route)
                        }
                        is LoginEffect.Navigation.ToForgotPassword -> {
                            navController.navigate(Route.ForgotPassword.path)
                        }
                    }
                }
            )
        }

        composable(
            route = Route.Register.path, // path = "register/{role}"
            arguments = Route.Register.navArgs
        ) { backStackEntry ->
            // [FIX]: Không cần lấy từ previousBackStackEntry nữa vì đã truyền qua argument
            // ViewModel sẽ tự lấy từ SavedStateHandle (đã làm ở RegisterViewModel)
            // Hoặc có thể truyền vào UI nếu muốn (nhưng RegisterScreen đã xóa preSelectedRole để ViewModel tự lo rồi, hoặc chưa?)
            // Kiểm tra RegisterScreen ở lượt 54:
            // fun RegisterScreen(..., viewModel: RegisterViewModel = hiltViewModel(), preSelectedRole: String = "CUSTOMER")
            // ViewModel đã lấy từ SavedStateHandle ở lượt 56.
            // Vậy ta cứ truyền argument vào cho chắc ăn nếu UI cần.
            
            val roleArg = backStackEntry.arguments?.getString(Route.Register.ARG_ROLE) ?: "CUSTOMER"
            
            RegisterScreen(
                onNavigation = { effect ->
                    when(effect) {
                        is RegisterEffect.Navigation.ToLogin -> {
                            navController.popBackStack()
                        }
                        is RegisterEffect.Navigation.ToHome -> {
                            navController.navigate(Route.Graph.CUSTOMER) {
                                popUpTo(Route.Graph.AUTH) { inclusive = true }
                            }
                        }
                    }
                },
                // Truyền role lấy từ URL vào UI (để UI init effect SelectRole nếu cần)
                preSelectedRole = roleArg
            )
        }
        composable(Route.ForgotPassword.path) { /* ForgotPasswordScreen */ }
    }
}

// 2. ADMIN GRAPH
fun NavGraphBuilder.adminGraph(navController: NavHostController) {
    navigation(startDestination = Route.AdminDashboard.path, route = Route.Graph.ADMIN) {

        composable(Route.AdminDashboard.path) {
            AdminDashboardScreen(navController = navController)
        }

        composable(Route.AdminStoreInfo.path) {
            AdminStoreInfoScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Route.AdminFoodList.path) {
            AdminFoodListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(Route.AddFood.path) },
                onNavigateToEdit = { foodId -> navController.navigate(Route.EditFood.createRoute(foodId)) }
            )
        }

        composable(Route.AdminCategoryList.path) {
            AdminCategoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate("admin_add_category") },
                onNavigateToEdit = { catId -> navController.navigate("admin_edit_category/$catId") }
            )
        }

        composable(Route.AdminOrderList.path) {
            AdminOrderListScreen(navController = navController)
        }

        composable(Route.AddFood.path) {
            AdminFoodDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.EditFood.path,
            arguments = Route.EditFood.navArgs
        ) {
            AdminFoodDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("admin_add_category") {
            AddEditCategoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

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

// 3. CUSTOMER GRAPH
fun NavGraphBuilder.customerGraph(navController: NavHostController) {
    navigation(startDestination = Route.CustomerHome.path, route = Route.Graph.CUSTOMER) {

        composable(Route.CustomerHome.path) {
            CustomerHomeScreen(navController = navController)
        }

        composable(
            route = Route.CustomerFoodList.path,
            arguments = Route.CustomerFoodList.navArgs
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString(Route.CustomerFoodList.ARG_TYPE) ?: "popular"
            FoodListScreen(navController = navController, type = type)
        }

        composable(
            route = Route.CustomerFoodDetail.path,
            arguments = Route.CustomerFoodDetail.navArgs
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString(Route.CustomerFoodDetail.ARG_FOOD_ID) ?: ""
            FoodDetailScreen(navController = navController, foodId = foodId)
        }

        composable(Route.CustomerCart.path) {
            CustomerCartScreen(navController = navController)
        }

        composable(Route.CustomerProfile.path) {
            CustomerProfileScreen(navController = navController)
        }

        composable(
            route = Route.CustomerTracking.path,
            arguments = Route.CustomerTracking.navArgs
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Route.CustomerTracking.ARG_ORDER_ID) ?: ""
            CustomerTrackingScreen(navController = navController, orderId = orderId)
        }

        composable(Route.CustomerFavorites.path) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Màn hình Yêu thích (Đang phát triển)")
            }
        }
        composable(Route.CustomerNotifications.path) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Màn hình Thông báo (Đang phát triển)")
            }
        }
        
        composable(Route.CustomerAddress.path) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Màn hình Địa chỉ (Đang phát triển)")
            }
        }
        composable(Route.CustomerOrderHistory.path) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Màn hình Lịch sử đơn hàng (Đang phát triển)")
            }
        }
        composable(Route.CustomerEditProfile.path) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Màn hình Chỉnh sửa hồ sơ (Đang phát triển)")
            }
        }
    }
}

// 4. DRIVER GRAPH
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
}