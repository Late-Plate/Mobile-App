package com.example.late_plate.view_model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.data.InventoryDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray

class InventoryViewModel(
    private val dataStore: InventoryDataStore
) : ViewModel(){
    private val _inventoryItems = mutableStateListOf<InventoryItem>()
    val inventoryItems: List<InventoryItem> get() = _inventoryItems

    var selectFromNER = mutableStateOf(false)

    private var _addOrUpdate: InventoryPopUpState = InventoryPopUpState.ADD

    val ingredientRegex = Regex("\\b(?:[A-Za-z-]+(?:\\s+[A-Za-z-]+)*)\\b")

    var addOrUpdate: InventoryPopUpState
        get() = _addOrUpdate
        set(value) {
            _addOrUpdate = value
        }

    private val _idsSet = mutableSetOf<Int>()

    lateinit var ingredients: List<String>

    init {
        viewModelScope.launch {
            loadInventory()
            _inventoryItems.forEach { item -> _idsSet.add(item.id) }

            Log.d("ID'S SET", _idsSet.toString())
        }
    }

    private suspend fun loadInventory() {
        val savedItems = dataStore.inventoryItemsFlow.first()
        _inventoryItems.clear()
        _inventoryItems.addAll(savedItems)
    }



    var showDialog by mutableStateOf(false)
        private set

    var selectedItem by mutableStateOf<InventoryItem?>(null)
        private set

    var selectedIndex by mutableStateOf<Int?>(null)
        private set


    fun openDialog(){
        showDialog = true
        Log.d("ADD", addOrUpdate.toString())
    }

    fun generateUniqueId(): Int {
        var newId = 0
        while (_idsSet.contains(newId)) {
            newId++
        }
        return newId
    }

    fun onConfirm(name: String, quantity: Float, type: String): String {
        if (selectedItem == null)
            return addItem(name, quantity, type)
        else
            return updateItem(name, quantity, type)
    }

    fun addItem(name: String, quantity: Float, type: String): String {
        if(!selectFromNER.value) return "FAIL"
        if(!validateInput(name, quantity)) return "NOT VALID"

        if(lookForSimilarItem(name, quantity, type)) return "SIMILAR"

        val newId = generateUniqueId()
        val newItem = InventoryItem(id = newId, title = name, quantity, type)

        _inventoryItems.add(newItem)
        _idsSet.add(newId)

        saveInventory()
        return "SUCCESS"
    }

    fun selectItem(item: InventoryItem, index: Int) {
        selectedItem = item
        selectedIndex = index
        showDialog = true
        selectFromNER.value = true
    }

    fun closeDialog() {
        showDialog = false
        selectedItem = null
        selectedIndex = null
        selectFromNER.value = false
    }

    fun deleteItem(item: InventoryItem){
        _inventoryItems.remove(item)
        saveInventory()
    }
    fun updateItem(newName: String, newQuantity: Float, newType: String): String {
        if(!selectFromNER.value) return "NULL"
        if(!validateInput(newName, newQuantity)) return "NOT VALID"
        if (selectedIndex in _inventoryItems.indices) {
            val oldId = _inventoryItems[selectedIndex!!].id
            _inventoryItems.removeAt(selectedIndex!!)
            Log.d("inventory list", _inventoryItems.toString())
            val updatedItem = InventoryItem(oldId, newName, newQuantity, newType)// Remove the old item
            _inventoryItems.add(selectedIndex!!, updatedItem) // Insert the updated item at the same index
            saveInventory()

        }
        return "SUCCESS"
    }
    private fun saveInventory() {
        viewModelScope.launch {
            dataStore.saveInventoryItems(_inventoryItems)
        }
    }

    private fun validateInput(name: String, quantity: Float): Boolean{
        if(quantity <= 0) return false
        if(!ingredientRegex.containsMatchIn(name.lowercase())) return false
        return true
    }

    private fun lookForSimilarItem(name: String, quantity: Float, type: String): Boolean{
        var updatedItem: InventoryItem? = null
        _inventoryItems.forEachIndexed { index, inventoryItem ->
            if(inventoryItem.title == name){
                val oldItem = inventoryItem
                _inventoryItems.removeAt(index)
                updatedItem = InventoryItem(
                    oldItem.id,
                    oldItem.title,
                    oldItem.quantity + quantity,
                    type
                )
                _inventoryItems.add(updatedItem!!)
                saveInventory()
                return true

            }
        }
        if(updatedItem == null) return false
        else return true
    }


}

enum class InventoryPopUpState{
    ADD, UPDATE
}


data class InventoryItem(
    val id: Int,
    val title: String,
    val quantity: Float,
    val unitType: String
)
