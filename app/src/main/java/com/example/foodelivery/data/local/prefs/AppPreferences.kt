package com.example.foodelivery.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore by preferencesDataStore("food_delivery_prefs")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val KEY_CART = stringPreferencesKey("cart_json")
    private val KEY_USER = stringPreferencesKey("user_json")

    // --- CART ---
    val cartFlow: Flow<List<CartItem>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_CART]
        if (json.isNullOrEmpty()) emptyList()
        else gson.fromJson(json, object : TypeToken<List<CartItem>>() {}.type)
    }

    suspend fun saveCart(cart: List<CartItem>) {
        val json = gson.toJson(cart)
        context.dataStore.edit { it[KEY_CART] = json }
    }

    suspend fun clearCart() {
        context.dataStore.edit { it.remove(KEY_CART) }
    }

    // --- USER ---
    val userFlow: Flow<User?> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_USER]
        if (json.isNullOrEmpty()) null
        else gson.fromJson(json, User::class.java)
    }

    suspend fun saveUser(user: User) {
        val json = gson.toJson(user)
        context.dataStore.edit { it[KEY_USER] = json }
    }

    suspend fun clearUser() {
        context.dataStore.edit { it.remove(KEY_USER) }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}