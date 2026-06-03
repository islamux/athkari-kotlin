package com.athkarix.app.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.athkarix.app.data.model.AthkarItem
import com.athkarix.app.data.service.AssmaHussnaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssmaHussnaViewModel(
    private val appContext: Context
) : BaseAthkarViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasError = MutableStateFlow(false)
    val hasError: StateFlow<Boolean> = _hasError.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _dataList = MutableStateFlow<List<AthkarItem>>(emptyList())
    override val dataList: List<AthkarItem> get() = _dataList.value

    override val maxPageCounters: List<Int> get() = List(_dataList.value.size) { 1 }

    override val completionMessage: String = "انهيت قراءة أسماء الله الحسنى"

    init {
        loadData()
    }

    fun loadData() {
        _isLoading.value = true
        _hasError.value = false
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = AssmaHussnaService.getAllAssmaHussna(appContext)
                _dataList.value = data.map { item ->
                    AthkarItem(duaText = "[${item.name}]\n\n${item.text}")
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _dataList.value = emptyList()
                _isLoading.value = false
                _hasError.value = true
                _errorMessage.value = e.message ?: "فشل تحميل البيانات"
            }
        }
    }

    fun searchByName(query: String) = AssmaHussnaService.searchByName(appContext, query)
    fun searchByText(query: String) = AssmaHussnaService.searchByText(appContext, query)
}
