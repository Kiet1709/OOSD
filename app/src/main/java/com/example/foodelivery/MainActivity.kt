package com.example.foodelivery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.foodelivery.ui.theme.FoodeliveryTheme
import com.example.foodelivery.ui.theme.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            FoodeliveryTheme {
                // Surface container sử dụng màu nền từ Theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Tạo NavController duy nhất cho toàn App
                    val navController = rememberNavController()

                    // 2. Gọi NavGraph và truyền Controller vào
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}