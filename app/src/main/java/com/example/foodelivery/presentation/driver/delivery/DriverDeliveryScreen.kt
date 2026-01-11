package com.example.foodelivery.presentation.driver.delivery

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodelivery.presentation.driver.delivery.contract.DriverDeliveryEffect
import com.example.foodelivery.presentation.driver.delivery.contract.DriverDeliveryIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDeliveryScreen(
    navController: NavController,
    viewModel: DriverDeliveryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DriverDeliveryEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is DriverDeliveryEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giao hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.order != null) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.setEvent(DriverDeliveryIntent.MarkAsDelivered) },
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    text = { Text("Đã giao hàng") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.order != null) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Từ nhà hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                Text(state.restaurant?.address ?: "")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Đến khách hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                Text(state.order?.shippingAddress ?: "")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                Text(state.customer?.name ?: "")
                            }
                        }
                    }
                }
            }
        }
    }
}