@file:OptIn(ExperimentalMaterialApi::class)

package com.example.late_plate.ui.screens.inventory

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.view_model.InventoryViewModel
import androidx.compose.material.ExperimentalMaterialApi
import com.example.late_plate.ui.components.SwipeToDeleteContainer



@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(modifier : Modifier = Modifier){

    val inventoryViewModel: InventoryViewModel = viewModel()

    val iconsList = listOf(
        R.drawable.inv_icon1,
        R.drawable.inv_icon2,
        R.drawable.inv_icon3,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inventory",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        }
    ) { innerPadding->
        Column(modifier = Modifier
            .padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
//                    .padding(innerPadding)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 70.dp),
            ) {
                itemsIndexed(
                    inventoryViewModel.inventoryItems,
                    key = { _, item -> item.id }
                ) { index, item ->
                    val randomIcon = iconsList[index % iconsList.size]
                    SwipeToDeleteContainer(
                        item = item,
                        onDelete = {
                            inventoryViewModel.deleteItem(item)
                        }
                    ) {
                        item ->
                        CustomCard(
                            modifier = Modifier.clickable {
                                inventoryViewModel.selectItem(item, index)
                                Log.d("Debug", "${item.title} is clicked")
                            }
                        ) {
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
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .padding(8.dp),
                                        text = "x ${if (item.quantity % 1 == 0f) item.quantity.toInt() else item.quantity}",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

            }
            if (inventoryViewModel.showDialog) {
                Log.d("attempt to update", inventoryViewModel.selectedItem.toString())
                CustomInventoryPopup(
                    showDialog = inventoryViewModel.showDialog,
                    onDismiss = { inventoryViewModel.closeDialog() },
                    onConfirm = { index, name, qty, type ->
                        if (index != null) {
                            inventoryViewModel.updateItem(index, name, qty, type)
                            inventoryViewModel.closeDialog()
                        }
                    },
                    modifier = Modifier,
                    name = inventoryViewModel.selectedItem?.title.orEmpty(),
                    quantity = inventoryViewModel.selectedItem?.quantity ?: 0f,
                    type = inventoryViewModel.selectedItem?.unitType.orEmpty(),
                    btnText = "Update",
                    index = inventoryViewModel.selectedIndex
                )
            }

        }




    }


}
//@Preview(showBackground = true, name = "inventory preview")
//@Composable
//fun InventoryPreview(){
//    InventoryScreen()
//}
