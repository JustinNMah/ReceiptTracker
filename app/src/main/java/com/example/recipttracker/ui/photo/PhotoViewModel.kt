package com.example.recipttracker.ui.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.domain.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoViewModel @Inject constructor(
    private val repository: ReceiptRepository
): ViewModel() {
    fun onEvent(event: PhotoEvent) {
        when(event) {
            is PhotoEvent.InsertReceipt -> {
                viewModelScope.launch {
                    repository.insertReceipt(event.receipt)
                }
            }
        }
    }
}