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

    private var _data: MutableLiveData<Set<String>> = MutableLiveData<Set<String>>()
    val data: LiveData<Set<String>> = _data

    private var _category: MutableLiveData<String> = MutableLiveData<String>()
    val category: LiveData<String> = _category

    private var _filePath: MutableLiveData<String> = MutableLiveData<String>()
    val filePath: LiveData<String> = _filePath

    // this field does not apply for adding a receipt
    private var _receiptToEdit: MutableLiveData<Receipt> = MutableLiveData<Receipt>()
    val receiptToEdit: LiveData<Receipt> = _receiptToEdit

    fun setReceiptToEdit(receiptToEdit: Receipt) {
        _mode.value = Mode.EDIT
        _receiptToEdit.value = receiptToEdit
        _store.value = receiptToEdit.store
        _amount.value = receiptToEdit.amount
        _date.value = receiptToEdit.date
        _category.value = receiptToEdit.category
        _filePath.value = receiptToEdit.filePath
    }

    fun setReceiptToAdd(filePath: String) {
        _mode.value = Mode.ADD
        _store.value = ""
        _amount.value = ""
        _date.value = ""
        _category.value = ""
        _filePath.value = filePath
    }
}