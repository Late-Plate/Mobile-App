package com.example.late_plate.view_model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class InventoryViewModel : ViewModel(){
    private val _inventoryItems = mutableStateListOf(
        InventoryItem(0, "Eggs", 12f, "unit"),
        InventoryItem(1, "Milk", 1f, "kg"),
        InventoryItem(2, "Flour", 0.5f, "kg"),
        InventoryItem(3, "Rice", 1.5f, "kg"),
        InventoryItem(4, "Banana", 5f, "unit"),
        InventoryItem(5, "Butter", 0.1f, "kg"),
        InventoryItem(6, "Cream cheese", 0.7f, "kg"),
        InventoryItem(7, "Tomato", 8f, "unit"),
        InventoryItem(8, "Carrot", 20f, "unit"),
    )

    private val _idsList = mutableListOf<Int>()

    init {
        _inventoryItems.forEach{item -> _idsList.add(item.id)}
        Log.d("ID'S LIST", _idsList.toString())
    }

    var showDialog by mutableStateOf(false)
        private set

    var selectedItem by mutableStateOf<InventoryItem?>(null)
        private set

    var selectedIndex by mutableStateOf<Int?>(null)
        private set

    val inventoryItems: List<InventoryItem> get() = _inventoryItems

    fun openDialog(){
        showDialog = true
    }

    fun addItem(name: String, quantity: Float, type: String){
        var newId = _idsList[_idsList.size - 1]
        newId++
        _inventoryItems.add(InventoryItem(id = newId, title = name, quantity, type))
        _idsList.add(newId)
    }

    fun selectItem(item: InventoryItem, index: Int) {
        selectedItem = item
        selectedIndex = index
        showDialog = true
    }

    fun closeDialog() {
        showDialog = false
        selectedItem = null
    }

    fun deleteItem(item: InventoryItem){
        _inventoryItems.remove(item)
    }
    fun updateItem(index: Int, newName: String, newQuantity: Float, newType: String) {
        if (index in _inventoryItems.indices) {
            val oldId = _inventoryItems[index].id
            _inventoryItems.removeAt(index)
            Log.d("inventory list", _inventoryItems.toString())
            val updatedItem = InventoryItem(oldId, newName, newQuantity, newType)// Remove the old item
            _inventoryItems.add(index, updatedItem) // Insert the updated item at the same index
        }
    }



}



data class InventoryItem(
    val id: Int,
    val title: String,
    val quantity: Float,
    val unitType: String
)

