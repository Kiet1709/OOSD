package com.example.foodelivery.core.base

/**
 * Design Pattern: Mapper / Adapter
 * Mục đích: Chuyển đổi dữ liệu giữa các lớp (Data <-> Domain <-> Presentation)
 * Giúp tách biệt sự phụ thuộc dữ liệu.
 */
interface Mapper<Input, Output> {
    fun map(input: Input): Output
}

// Interface cho việc map danh sách (có default implementation)
interface ListMapper<Input, Output> : Mapper<List<Input>, List<Output>> {
    override fun map(input: List<Input>): List<Output> {
        return input.map { mapItem(it) }
    }
    
    // Hàm con cần implement
    fun mapItem(input: Input): Output
}