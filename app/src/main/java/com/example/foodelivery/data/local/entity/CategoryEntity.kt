package com.example.foodelivery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodelivery.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String
) {
    fun toDomainModel(): Category {
        return Category(
            id = id,
            name = name,
            imageUrl = imageUrl
        )
    }

    companion object {
        fun fromDomainModel(category: Category): CategoryEntity {
            return CategoryEntity(
                id = category.id,
                name = category.name,
                imageUrl = category.imageUrl
            )
        }
    }
}