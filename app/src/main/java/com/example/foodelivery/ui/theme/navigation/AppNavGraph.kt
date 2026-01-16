package com.example.foodelivery.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.foodelivery.presentation.admin.category.add_edit.AddEditCategoryScreen
import com.example.foodelivery.presentation.admin.category.list.AdminCategoryListScreen
import com.example.foodelivery.presentation.admin.home.AdminDashboardScreen
import com.example.foodelivery.presentation.auth.contract.LoginEffect
import com.example.foodelivery.presentation.auth.forgot_password.ForgotPasswordScreen
import com.example.foodelivery.presentation.auth.login.LoginScreen
import com.example.foodelivery.presentation.auth.register.RegisterScreen
import com.example.foodelivery.presentation.auth.register.contract.RegisterEffect
import com.example.foodelivery.presentation.customer.cart.CustomerCartScreen
import com.example.foodelivery.presentation.customer.checkout.CheckoutScreen
import com.example.foodelivery.presentation.customer.food.detail.FoodDetailScreen
import com.example.foodelivery.presentation.customer.food.list.CustomerFoodListScreen
import com.example.foodelivery.presentation.customer.home.CustomerHomeScreen
import com.example.foodelivery.presentation.customer.orderdetail.OrderDetailScreen
import com.example.foodelivery.presentation.customer.orderhistory.OrderHistoryScreen
import com.example.foodelivery.presentation.customer.profile.CustomerProfileScreen
import com.example.foodelivery.presentation.customer.profile.editprofile.CustomerEditProfileScreen
import com.example.foodelivery.presentation.customer.settings.CustomerSettingsScreen
import com.example.foodelivery.presentation.driver.dashboard.DriverDashboardScreen
import com.example.foodelivery.presentation.driver.delivery.DriverDeliveryScreen
import com.example.foodelivery.presentation.driver.profile.DriverProfileScreen
import com.example.foodelivery.presentation.driver.profile.editprofile.DriverEditProfileScreen
import com.example.foodelivery.presentation.restaurant.food.detail.RestaurantAddEditFoodScreen
import com.example.foodelivery.presentation.restaurant.food.list.RestaurantFoodListScreen
import com.example.foodelivery.presentation.restaurant.home.RestaurantDashboardScreen
import com.example.foodelivery.presentation.restaurant.order_list.RestaurantOrderListScreen
import com.example.foodelivery.presentation.restaurant.profile.edit.RestaurantEditProfileScreen
import com.example.foodelivery.presentation.restaurant.profile.view.RestaurantProfileScreen

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
        restaurantGraph(navController)
        customerGraph(navController)
        driverGraph(navController)
    }
}

// AUTH
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = Route.Login.path, route = Route.Graph.AUTH) {
        composable(Route.Login.path) {
            LoginScreen(
                onNavigation = { effect ->
                    when (effect) {
                        is LoginEffect.Navigation.ToCustomerHome -> navController.navigate(Route.Graph.CUSTOMER) { popUpTo(Route.Graph.AUTH) { inclusive = true } }
                        is LoginEffect.Navigation.ToDriverDashboard -> navController.navigate(Route.Graph.DRIVER) { popUpTo(Route.Graph.AUTH) { inclusive = true } }
                        is LoginEffect.Navigation.ToAdminDashboard -> navController.navigate(Route.Graph.ADMIN) { popUpTo(Route.Graph.AUTH) { inclusive = true } }
                        is LoginEffect.Navigation.ToRestaurantDashboard -> navController.navigate(Route.Graph.RESTAURANT) { popUpTo(Route.Graph.AUTH) { inclusive = true } }
                        is LoginEffect.Navigation.ToRegister -> navController.navigate(Route.Register.path)
                        is LoginEffect.Navigation.ToForgotPassword -> navController.navigate(Route.ForgotPassword.path)
                        else -> {}
                    }
                }
            )
        }
        composable(Route.Register.path) {
            RegisterScreen(onNavigation = {
                effect ->
                when (effect) {
                    is RegisterEffect.Navigation.ToLogin -> navController.popBackStack()
                    else -> {}
                }
            })
        }
        composable(Route.ForgotPassword.path) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

// ADMIN
fun NavGraphBuilder.adminGraph(navController: NavHostController) {
    navigation(startDestination = Route.AdminDashboard.path, route = Route.Graph.ADMIN) {
        composable(Route.AdminDashboard.path) { AdminDashboardScreen(navController = navController) }
        composable(Route.AdminCategoryList.path) {
            AdminCategoryListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate("admin_add_category") },
                onNavigateToEdit = {}
            )
        }
        composable("admin_add_category") { AddEditCategoryScreen(onNavigateBack = { navController.popBackStack() }) }
    }
}

// RESTAURANT
fun NavGraphBuilder.restaurantGraph(navController: NavHostController) {
    navigation(startDestination = Route.RestaurantDashboard.path, route = Route.Graph.RESTAURANT) {
        composable(Route.RestaurantDashboard.path) { RestaurantDashboardScreen(navController = navController) }
        composable(Route.RestaurantFoodList.path) { RestaurantFoodListScreen(navController = navController) }
        composable(Route.RestaurantAddEditFood.path) { RestaurantAddEditFoodScreen(navController = navController) }
        composable(Route.RestaurantProfile.path) { RestaurantProfileScreen(navController = navController) }
        composable(Route.RestaurantEditProfile.path) { RestaurantEditProfileScreen(navController = navController) }
        composable("restaurant_order_list") { RestaurantOrderListScreen(navController = navController) } 
    }
}

// CUSTOMER
fun NavGraphBuilder.customerGraph(navController: NavHostController) {
    navigation(startDestination = Route.CustomerHome.path, route = Route.Graph.CUSTOMER) {
        composable(Route.CustomerHome.path) { CustomerHomeScreen(navController = navController) }
        composable(
            route = Route.CustomerFoodDetail.path,
            arguments = Route.CustomerFoodDetail.navArgs
        ) {
            FoodDetailScreen(navController = navController)
        }
        composable(Route.CustomerCart.path) { CustomerCartScreen(navController = navController) }
        composable(
            route = Route.CustomerFoodList.path,
            arguments = Route.CustomerFoodList.navArgs
        ) {
            CustomerFoodListScreen(navController = navController)
        }
        composable(Route.CustomerProfile.path) { CustomerProfileScreen(navController = navController) }

        composable(Route.CustomerEditProfile.path) { CustomerEditProfileScreen(navController = navController) }

        composable(
            route = Route.Checkout.routeWithArgs,
            arguments = listOf(
                navArgument(Route.Checkout.ARG_ADDRESS) { type = NavType.StringType }
            )
        ) {
            CheckoutScreen(navController = navController)
        }

        composable(Route.CustomerOrderHistory.path) { OrderHistoryScreen(navController = navController) }

        composable(route = "order_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(navController = navController, orderId = orderId)
        }

        composable(Route.CustomerSettings.path) {
            CustomerSettingsScreen(navController = navController)

        }
    }
}

// DRIVER
fun NavGraphBuilder.driverGraph(navController: NavHostController) {
    navigation(startDestination = Route.DriverDashboard.path, route = Route.Graph.DRIVER) {
        composable(Route.DriverDashboard.path) { DriverDashboardScreen(navController = navController) }
        composable(Route.DriverDelivery.path) { DriverDeliveryScreen(navController = navController) }
        composable(Route.DriverProfile.path) { DriverProfileScreen(navController = navController) } // Add this
        composable(Route.DriverEditProfile.path) { DriverEditProfileScreen(navController = navController) } // Add this
    }
}