package com.example.foodelivery.core.common

import android.content.Context
import android.view.View
import android.widget.Toast
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Format tiền tệ: 50000.0 -> "50.000 đ"
fun Double?.toVndCurrency(): String {
    val value = this ?: 0.0
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return "${formatter.format(value)} đ"
}

// Format ngày: Timestamp -> "12/05/2025 14:30"
fun Long.toReadableDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

// Ẩn/Hiện View
fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

// 2. Flow Collection an toàn cho Fragment
fun <T> Fragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}
// Validate Email
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}