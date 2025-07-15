package com.example.recipttracker.ui.addEditReceipt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipttracker.domain.model.Receipt

class ModifyReceiptVM(): ViewModel() {
    private var _mode: MutableLiveData<Mode> = MutableLiveData<Mode>()
    val mode: LiveData<Mode> = _mode

    private var _store: MutableLiveData<String> = MutableLiveData<String>()
    val store: LiveData<String> = _store

    private var _amount: MutableLiveData<String> = MutableLiveData<String>()
    val amount: LiveData<String> = _amount

    private var _date: MutableLiveData<String> = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private var _category: MutableLiveData<String> = MutableLiveData<String>()
    val category: LiveData<String> = _category

    private var _uriPath: MutableLiveData<String> = MutableLiveData<String>()
    val uriPath: LiveData<String> = _uriPath

    // this field only applies to adding a receipt (not edit)
    private var _receiptId: MutableLiveData<Int> = MutableLiveData<Int>()
    val receiptId: LiveData<Int> = _receiptId

    fun setReceiptToEdit(receiptToEdit: Receipt) {
        _mode.value = Mode.EDIT
        _receiptId.value = receiptToEdit.id!! // receiptToEdit.id should not be null as it is a primary key?
        _store.value = receiptToEdit.store
        _amount.value = receiptToEdit.amount
        _date.value = receiptToEdit.date
        _category.value = receiptToEdit.category
        _uriPath.value = receiptToEdit.uriPath
    }

    fun setReceiptToAdd(uriPath: String) {
        _mode.value = Mode.ADD
        _store.value = ""
        _amount.value = ""
        _date.value = ""
        _category.value = ""
        _uriPath.value = uriPath
    }
}