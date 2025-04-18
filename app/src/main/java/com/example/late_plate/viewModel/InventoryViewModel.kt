package com.example.late_plate.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.late_plate.data.InventoryDataStore
import com.example.late_plate.dummy.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.math.BigDecimal
import java.math.RoundingMode

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val dataStore: InventoryDataStore
) : ViewModel(){
    private val _inventoryItems = mutableStateListOf<InventoryItem>()
    val inventoryItems: List<InventoryItem> get() = _inventoryItems

    private val _groceryList = mutableStateListOf<InventoryItem>()
    val groceryList: List<InventoryItem> get() = _groceryList

    var selectFromNER = mutableStateOf(false)

    private var _addOrUpdate: InventoryPopUpState = InventoryPopUpState.ADD

    val ingredientRegex = Regex("\\b(?:[A-Za-z-]+(?:\\s+[A-Za-z-]+)*)\\b")

    var addOrUpdate: InventoryPopUpState
        get() = _addOrUpdate
        set(value) {
            _addOrUpdate = value
        }

    val kitchenMetrics = mapOf(
        "cup" to 0.25f,
        "cups" to 0.25f,
        "teaspoon" to 0.005f,
        "tsp" to 0.005f,
        "tablespoon" to 0.015f,
        "tbsp" to 0.015f
    )

    private val _idsSetInventory = mutableSetOf<Int>()
    private val _idsSetGrocery = mutableSetOf<Int>()

    val fractionRegex = Regex("^\\s*\\d+(\\.\\d+)?\\s*/\\s*\\d+(\\.\\d+)?\\s*$")



    var showInventoryDialog by mutableStateOf(false)
        private set

    var showGroceryListDialog by mutableStateOf(false)
        private set

    var selectedItem by mutableStateOf<InventoryItem?>(null)
        private set

    var selectedIndex by mutableStateOf<Int?>(null)
        private set


    init {
        viewModelScope.launch {
            loadInventory()
            loadGroceryList()
            _inventoryItems.forEach { item -> _idsSetInventory.add(item.id) }
            _groceryList.forEach { item -> _idsSetGrocery.add(item.id) }

            Log.d("GROCERY", _groceryList.toString())

            Log.d("ID'S SET", _idsSetInventory.toString())
        }
    }

    private suspend fun loadInventory() {
        val savedItems = dataStore.inventoryItemsFlow.first()
        _inventoryItems.clear()
        _inventoryItems.addAll(savedItems)
    }
    private suspend fun loadGroceryList() {
        val savedGroceryItems = dataStore.groceryItemsFlow.first()
        _groceryList.clear()
        _groceryList.addAll(savedGroceryItems)
    }

    private fun saveInventory() {
        viewModelScope.launch {
            dataStore.saveInventoryItems(_inventoryItems)
        }
    }
    private fun saveGroceryList() {
        viewModelScope.launch {
            dataStore.saveGroceryItems(_groceryList)
        }
    }

    fun fractionToDecimal(fraction: String): Double {
        val parts = fraction.split("/")
        if (parts.size == 2) {
            val numerator = parts[0].toDouble()
            val denominator = parts[1].toDouble()
            return numerator / denominator
        }
        throw IllegalArgumentException("Invalid fraction format")
    }


    fun addRecipeIngredientsToGroceryList(ingredientsList: List<String>){
        val ingredients = extractIngredients(ingredientsList)

        //find ingredients already in inventory and skip them
        val willBeAddedIngredients = mutableListOf<IngredientEntry>()
        ingredients.forEachIndexed { index, ingredient->
            inventoryItems.forEach {item->
                if(item.title.equals(ingredient.ingredient)
                    && item.unitType.lowercase().equals(ingredient.quantityType)){
                    if(item.quantity >= ingredient.quantity) {
                        Log.d("IN INVENTORY", ingredient.toString())
                        return@forEachIndexed
                    }
                    else{
                        val newQuantity = ingredient.quantity - item.quantity
                        ingredient.quantity = newQuantity
                        willBeAddedIngredients.add(ingredient)
                        return@forEachIndexed
                    }
                }
            }

            willBeAddedIngredients.add(ingredient)

        }

        //add them in grocery list
        willBeAddedIngredients.forEach { ingredient->
            val newId = generateUniqueId(_idsSetGrocery)
            _groceryList.add(InventoryItem(
                id = newId,
                title = ingredient.ingredient,
                quantity = ingredient.quantity.toFloat(),
                unitType = ingredient.quantityType
            ))
            _idsSetGrocery.add(newId)

        }

        saveGroceryList()

        Log.d("ADDED", willBeAddedIngredients.toString())
        Log.d("ADDED", ingredients.toString())
    }

    private fun extractIngredients(ingredientsList: List<String>): MutableList<IngredientEntry> {
        val ingredients = mutableListOf<IngredientEntry>()
        val quantityType = StringBuilder()

        ingredientsList.forEach { ingredient->
            quantityType.clear()
            var quantity = 0.0
            var splitWords = ingredient.split(" ")

            splitWords.forEachIndexed { index, word ->
                val hasDigits = word.contains(Regex("\\d"))
                if (hasDigits) {
                    if (fractionRegex.matches(word)) {
                        quantity += fractionToDecimal(word)
                    } else {
                        quantity += word.toDouble()
                    }
                } else {
                    var indexToStartFrom = 0
                    if (kitchenMetrics.containsKey(word)) {
                        quantity *= kitchenMetrics[word]!!
                        quantity = BigDecimal(quantity).setScale(3, RoundingMode.HALF_UP).toDouble()
                        indexToStartFrom = index + 1
                        quantityType.append("kg")
                    }
                    else{
                        if(word.lowercase().equals("packet")) {
                            quantityType.append(word.lowercase())
                            indexToStartFrom = index + 1
                        }
                        else {
                            quantityType.append("unit")
                            indexToStartFrom = index
                        }

                    }
                    val ingredientWords = splitWords.subList(indexToStartFrom, splitWords.size)
                    val extractedIngredient = ingredientWords.joinToString(" ")

                    if(extractedIngredient.lowercase().contains("water"))
                        return@forEach
                    if(quantity == 0.0)
                        quantity += 1


                    ingredients.add(IngredientEntry(extractedIngredient.lowercase(), quantity, quantityType.toString()))
                    println("Quantity: $quantity, Ingredient: $extractedIngredient")
                    return@forEach
                }
            }

        }
        return ingredients
    }






    fun openInventoryDialog(){
        showInventoryDialog = true
        Log.d("ADD", addOrUpdate.toString())
    }
    fun openGroceryDialog(){
        showGroceryListDialog = true
    }

    fun generateUniqueId(_idsSet: Set<Int>): Int {
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
        if(!validateInput(name, quantity, type)) return "NOT VALID"

        if(lookForSimilarItemInventory(name, quantity, type)) return "SIMILAR"

        val newId = generateUniqueId(_idsSetInventory)
        val newItem = InventoryItem(id = newId, title = name, quantity, type)

        _inventoryItems.add(newItem)
        _idsSetInventory.add(newId)

        saveInventory()
        return "SUCCESS"
    }
    fun addGroceryItem(name: String, quantity: Float, type: String):String{
        if(!validateInput(name, quantity, type)) return "NOT VALID"
        Log.d("VALID", "$name $quantity $type")
        if(lookForSimilarItemGrocery(name, quantity, type)) return "SIMILAR"
        val newId = generateUniqueId(_idsSetGrocery)
        val newItem = InventoryItem(id = newId, title = name, quantity, type)

        _groceryList.add(newItem)
        _idsSetGrocery.add(newId)
        saveGroceryList()
        return "SUCCESS"
    }

    fun removeGroceryItem(item: InventoryItem) {
        _groceryList.remove(item)
        saveGroceryList()
    }


    fun selectItem(item: InventoryItem, index: Int) {
        selectedItem = item
        selectedIndex = index
        showInventoryDialog = true
        selectFromNER.value = true
    }

    fun closeInventoryDialog() {
        showInventoryDialog = false
        selectedItem = null
        selectedIndex = null
        selectFromNER.value = false
    }

    fun closeGroceryListDialog() {
        showGroceryListDialog = false
    }

    fun deleteItem(item: InventoryItem){
        _inventoryItems.remove(item)
        saveInventory()
    }
    fun updateItem(newName: String, newQuantity: Float, newType: String): String {
        if(!selectFromNER.value) return "NULL"
        if(!validateInput(newName, newQuantity, newType)) return "NOT VALID"
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



    private fun validateInput(name: String, quantity: Float, type: String): Boolean{
        Log.d("QUANTITY", type)
        if(quantity <= 0) return false
        if(!ingredientRegex.containsMatchIn(name.lowercase())) return false
        if(type.isEmpty()) return false
        return true
    }

    private fun lookForSimilarItemInventory(name: String, quantity: Float, type: String): Boolean{
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

    private fun lookForSimilarItemGrocery(name: String, quantity: Float, type: String): Boolean{
        var updatedItem: InventoryItem? = null
        _groceryList.forEachIndexed { index, inventoryItem ->
            if(inventoryItem.title == name){
                val oldItem = inventoryItem
                _groceryList.removeAt(index)
                updatedItem = InventoryItem(
                    oldItem.id,
                    oldItem.title,
                    oldItem.quantity + quantity,
                    type
                )
                _groceryList.add(updatedItem!!)
                saveGroceryList()
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
data class IngredientEntry(
    val ingredient: String,
    var quantity: Double,
    val quantityType: String
)