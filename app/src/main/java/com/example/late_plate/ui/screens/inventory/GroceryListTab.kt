package com.example.late_plate.ui.screens.inventory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.SwipeToDeleteContainer
import com.example.late_plate.viewModel.InventoryPopUpState
import com.example.late_plate.viewModel.InventoryViewModel

@Composable
fun GroceryListTab(inventoryViewModel: InventoryViewModel,
                   iconsList: List<Int>,
){
    var showAlert by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().padding(top = 16.dp).padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.primary_transparent))
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Make sure the quantity is NOT in grams!",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 70.dp),
        ) {
            itemsIndexed(
                inventoryViewModel.groceryList,
                key = { _, item -> item.id }
            ) { index, item ->
                val randomIcon = iconsList[index % iconsList.size]
                SwipeToDeleteContainer(
                    item = item,
                    onDelete = {
                        inventoryViewModel.removeGroceryItem(item)
                    }
                ) {
                        item ->
                    CustomCard{
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(randomIcon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(0.75f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(2f),
                                text = item.title,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier
                                .width(16.dp)
                                .weight(0.1f))
                            Box(
                                contentAlignment = Alignment.CenterEnd,
                                modifier = Modifier.weight(1.4f)
                            ) {
                                Column(modifier = Modifier
                                    .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "x ${if (item.quantity % 1 == 0f) item.quantity.toInt() else item.quantity}",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    )
                                    Text(
                                        text = item.unitType,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }
        if (inventoryViewModel.showGroceryListDialog) {
            var confirmStatus: String = ""
            CustomInventoryPopup(
                showDialog = inventoryViewModel.showGroceryListDialog,
                onDismiss = { inventoryViewModel.closeGroceryListDialog() },
                onConfirm = {name, qty, type ->
                    confirmStatus =  inventoryViewModel.addGroceryItem(name, qty, type)

                    if(confirmStatus == "NOT VALID")
                        showAlert = true

                    else
                        inventoryViewModel.closeGroceryListDialog()


                    Log.d("GROCERY_LIST UPDATED", inventoryViewModel.groceryList.toString())
                },
                modifier = Modifier,
                name = inventoryViewModel.selectedItem?.title.orEmpty(),
                quantity = inventoryViewModel.selectedItem?.quantity ?: 0f,
                type = inventoryViewModel.selectedItem?.unitType.orEmpty(),
                status = InventoryPopUpState.ADD,
                selectNER = inventoryViewModel.selectFromNER,
                onEdit = { newval-> listOf("") },
                dialogType = "Grocery List"
            )
            if (showAlert) {
                AlertForInvalidity (isGrocery = true){showAlert = false }
            }
        }

    }
}
@Preview
@Composable
fun previewGrocery(){
//    GroceryListTab()
}