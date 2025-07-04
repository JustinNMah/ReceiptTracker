package com.example.recipttracker.domain.util

enum class SortField {
    DATE,
    STORE,
    CATEGORY
}

data class ReceiptSortOrder(
    val field: SortField,
    val isAscending: Boolean = true
)
