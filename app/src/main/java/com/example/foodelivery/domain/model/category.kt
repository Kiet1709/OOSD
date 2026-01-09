package com.example.foodelivery.domain.model

data class Category(
    val id: String,
    val name: String,
    val imageUrl: String
) {
    // Constructor rỗng cho Firestore
    constructor() : this("", "", "")
}