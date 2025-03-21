package com.example.late_plate.ui.screens.inventory

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.late_plate.view_model.InventoryViewModel

@Composable
fun AddItemPopup(modifier: Modifier){
    val inventoryViewModel: InventoryViewModel = viewModel()
    CustomInventoryPopup(
        modifier = modifier,
        showDialog = inventoryViewModel.showDialog,
        onDismiss = { inventoryViewModel.closeDialog() },
        onConfirm = { index, name, qty, type ->
            if(index == null)
                inventoryViewModel.addItem(name, qty, type) // Pass ViewModel function
            inventoryViewModel.closeDialog()
        },
        height = 0.46f,
        index = null
    )
}
