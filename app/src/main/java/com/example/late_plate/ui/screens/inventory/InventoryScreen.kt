@file:OptIn(ExperimentalMaterialApi::class)

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.late_plate.R
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.viewModel.InventoryViewModel
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.SwipeToDeleteContainer
import com.example.late_plate.ui.screens.FABState
import com.example.late_plate.viewModel.InventoryPopUpState


@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    modifier: Modifier = Modifier,
    onEdit: (String) -> List<String>,
    fabState: FABState
) {

    var showAlert by remember { mutableStateOf(false) }
    fabState.run {
        changeFAB(newIcon = Icons.Rounded.Add, newOnClick = {

            inventoryViewModel.addOrUpdate = InventoryPopUpState.ADD
            inventoryViewModel.openDialog()


        })
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp).padding(top = 8.dp)
    ) {
        Text(
            "Inventory",
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold

        )
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                inventoryViewModel.inventoryItems,
                key = { _, item -> item.id }
            ) { index, item ->

                SwipeToDeleteContainer(
                    item = item,
                    onDelete = {
                        inventoryViewModel.deleteItem(item)
                    }
                ) {
                    CustomCard(
                        contentPadding = 8,
                        onClick = {
                            inventoryViewModel.selectItem(item, index)
                            inventoryViewModel.addOrUpdate = InventoryPopUpState.UPDATE
                            Log.d("Debug", "${item.title} is clicked")
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier.weight(1f)
                                    .padding(horizontal = 8.dp),
                                text = item.title,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                text = "${if (item.quantity % 1 == 0f) item.quantity.toInt() else item.quantity} ${item.unitType} ",
                                color = MaterialTheme.colorScheme.onSurface,

                                fontSize = 16.sp
                            )

                        }
                    }
                }

            }
            item { Spacer(modifier = Modifier.height(86.dp)) }

        }
        if (inventoryViewModel.showDialog) {
            var confirmStatus: String = ""
            Log.d("attempt to update", inventoryViewModel.selectedItem.toString())
            CustomInventoryPopup(
                showDialog = inventoryViewModel.showDialog,
                onDismiss = { inventoryViewModel.closeDialog() },
                onConfirm = { name, qty, type ->
                    if (inventoryViewModel.selectFromNER.value) {
                        confirmStatus = inventoryViewModel.onConfirm(name, qty, type)
                    }
                    if (confirmStatus != "SUCCESS" && confirmStatus != "SIMILAR") {
                        showAlert = true
                    } else {
                        inventoryViewModel.closeDialog()
                    }
                },
                modifier = Modifier,
                name = inventoryViewModel.selectedItem?.title.orEmpty(),
                quantity = inventoryViewModel.selectedItem?.quantity ?: 0f,
                type = inventoryViewModel.selectedItem?.unitType.orEmpty(),
                status = inventoryViewModel.addOrUpdate,
                selectNER = inventoryViewModel.selectFromNER,
                onEdit = onEdit
            )
            if (showAlert) {
                AlertForInvalidity { showAlert = false }
            }
        }

    }


}

@Composable
fun AlertForInvalidity(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Text("Oops", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))

                Text(
                    text = "Please make sure to select an item from suggestions list and provide a valid quantity",
                    textAlign = TextAlign.Center,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                CustomButton(
                    onClick = onDismiss,
                    content = {
                        Text(
                            text = "Ok",
                        )
                    }

                )
            }

        }
    }
}

@Preview
@Composable
fun PreviewAlert() {
    AlertForInvalidity {}
}
