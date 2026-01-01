package com.example.foodelivery.presentation.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomBarScreen(
        route = Route.CustomerHome.path,
        title = "Home",
        icon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    object Orders : BottomBarScreen(
        route = Route.CustomerOrderHistory.path,
        title = "Đơn hàng",
        icon = Icons.Filled.Receipt,
        unselectedIcon = Icons.Filled.ReceiptLong
    )
    object Favorites : BottomBarScreen(
        route = Route.CustomerFavorites.path,
        title = "Đã thích",
        icon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Filled.FavoriteBorder
    )
    object Notifications : BottomBarScreen(
        route = Route.CustomerNotifications.path,
        title = "Thông báo",
        icon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Filled.NotificationsNone
    )
    object Profile : BottomBarScreen(
        route = Route.CustomerProfile.path,
        title = "Tôi",
        icon = Icons.Filled.Person,
        unselectedIcon = Icons.Filled.PersonOutline
    )
}

@Composable
fun CustomerBottomBar(navController: NavController) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Orders,
        BottomBarScreen.Favorites,
        BottomBarScreen.Notifications,
        BottomBarScreen.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    
    AnimatedVisibility(
        visible = bottomBarDestination,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = PrimaryColor
        ) {
            screens.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    label = { Text(text = screen.title) },
                    icon = {
                        Icon(
                            imageVector = if (selected) screen.icon else screen.unselectedIcon,
                            contentDescription = "Navigation Icon"
                        )
                    },
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryColor,
                        selectedTextColor = PrimaryColor,
                        indicatorColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}