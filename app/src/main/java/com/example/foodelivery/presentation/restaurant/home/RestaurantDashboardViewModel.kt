package com.example.foodelivery.presentation.restaurant.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.restaurant.home.contract.RestaurantDashboardEffect
import com.example.foodelivery.presentation.restaurant.home.contract.RestaurantDashboardIntent
import com.example.foodelivery.presentation.restaurant.home.contract.RestaurantDashboardState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class RestaurantDashboardViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val orderRepository: IOrderRepository
) : BaseViewModel<RestaurantDashboardState, RestaurantDashboardIntent, RestaurantDashboardEffect>(RestaurantDashboardState()) {

    init {
        handleIntent(RestaurantDashboardIntent.LoadData)
    }

    private fun loadRestaurantInfo() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val userResult = userRepository.getUser(uid)
            if (userResult is Resource.Success) {
                setState {
                    copy(
                        restaurantName = userResult.data?.name ?: "",
                        avatarUrl = userResult.data?.avatarUrl ?: ""
                    )
                }
            }
        }
    }

    private fun loadRevenueData() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            orderRepository.getAllOrders().collectLatest { resource ->
                if (resource is Resource.Success) {
                    val orders = resource.data?.filter { it.restaurantId == uid } ?: emptyList()
                    val totalRevenue = orders.filter { it.status == OrderStatus.DELIVERED || it.status == OrderStatus.DELIVERING }.sumOf { it.totalPrice }
                    val todayRevenue = orders.filter { isToday(it.timestamp) && (it.status == OrderStatus.DELIVERED || it.status == OrderStatus.DELIVERING) }.sumOf { it.totalPrice }
                    setState {
                        copy(
                            todayRevenue = todayRevenue,
                            totalRevenue = totalRevenue
                        )
                    }
                }
            }
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        calendar.timeInMillis = timestamp
        val orderDay = calendar.get(Calendar.DAY_OF_YEAR)
        return today == orderDay
    }

    fun setEvent(intent: RestaurantDashboardIntent) = handleIntent(intent)

    override fun handleIntent(intent: RestaurantDashboardIntent) {
        when (intent) {
            RestaurantDashboardIntent.ClickLogout -> logout()
            RestaurantDashboardIntent.LoadData -> {
                loadRestaurantInfo()
                loadRevenueData()
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setEffect { RestaurantDashboardEffect.NavigateToLogin }
        }
    }
}