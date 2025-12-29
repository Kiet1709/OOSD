package com.example.foodelivery.core.base

/**
 * Interface đánh dấu cho State (Trạng thái màn hình)
 * Ví dụ: HomeState, LoginState...
 */
interface ViewState

/**
 * Interface đánh dấu cho Intent (Hành động của người dùng)
 * Ví dụ: ButtonClicked, TextChanged...
 */
interface ViewIntent

/**
 * Interface đánh dấu cho Side Effect (Sự kiện 1 lần)
 * Ví dụ: ShowToast, Navigate...
 */
interface ViewSideEffect