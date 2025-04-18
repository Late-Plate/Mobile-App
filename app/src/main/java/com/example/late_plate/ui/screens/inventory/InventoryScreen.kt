@file:OptIn(ExperimentalMaterialApi::class)

package com.example.late_plate.ui.screens.inventory
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager // if needed

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.example.late_plate.ui.components.SwipeToDeleteContainer
import com.example.late_plate.viewModel.InventoryPopUpState
import kotlinx.coroutines.launch


import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.foundation.pager.PagerDefaults

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.colorResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    modifier: Modifier = Modifier,
    onEdit: (String) -> List<String>,
    pagerState: PagerState
) {

    val tabTitles = listOf("Inventory", "Grocery List")



    val coroutineScope = rememberCoroutineScope()

    var showAlert by remember { mutableStateOf(false) }

    val iconsList = listOf(
        R.drawable.inv_icon1,
        R.drawable.inv_icon2,
        R.drawable.inv_icon3,
    )
    val tabIcons = listOf(
        R.drawable.closed_fridge,
        R.drawable.open_fridge_icon,
        R.drawable.shopping_cart_icon
    )


    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    selectedContentColor = colorResource(R.color.primary),
                    unselectedContentColor = Color.Gray,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.background
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (title == "Inventory") {
                                Icon(
                                    painter = painterResource(
                                        id = if (pagerState.currentPage == index) tabIcons[1] else tabIcons[0]
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Icon(
                                    painter = painterResource(
                                        id = tabIcons[2]
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = title,
                                fontSize = 20.sp,
                                color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )

                        }

                    }
                }
            }
        }
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) { page ->
            Spacer(Modifier.height(16.dp))
            when (page) {
                0 -> InventoryTab(inventoryViewModel, iconsList) { newVal -> onEdit(newVal) }
                1 -> GroceryListTab(inventoryViewModel, iconsList)
            }
        }

    }
}


//    InventoryTab(inventoryViewModel, iconsList) {newVal-> onEdit(newVal) }

//    Column() {
//        LazyColumn(
//            modifier = Modifier
////                    .padding(innerPadding)
//                .padding(horizontal = 8.dp)
//                .padding(bottom = 70.dp),
//        ) {
//            itemsIndexed(
//                inventoryViewModel.inventoryItems,
//                key = { _, item -> item.id }
//            ) { index, item ->
//                val randomIcon = iconsList[index % iconsList.size]
//                SwipeToDeleteContainer(
//                    item = item,
//                    onDelete = {
//                        inventoryViewModel.deleteItem(item)
//                    }
//                ) {
//                        item ->
//                    CustomCard(
//                        modifier = Modifier.clickable {
//                            inventoryViewModel.selectItem(item, index)
//                            inventoryViewModel.addOrUpdate = InventoryPopUpState.UPDATE
//                            Log.d("Debug", "${item.title} is clicked")
//                        }
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Icon(
//                                painter = painterResource(randomIcon),
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.primary,
//                                modifier = Modifier.weight(0.75f)
//                            )
//                            Spacer(modifier = Modifier.width(16.dp))
//                            Text(
//                                modifier = Modifier
//                                    .padding(8.dp)
//                                    .weight(2f),
//                                text = item.title,
//                                color = MaterialTheme.colorScheme.onPrimary,
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 22.sp
//                            )
//                            Spacer(modifier = Modifier
//                                .width(16.dp)
//                                .weight(0.1f))
//                            Box(
//                                contentAlignment = Alignment.CenterEnd,
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                Text(
//                                    modifier = Modifier
//                                        .padding(8.dp),
//                                    text = "x ${if (item.quantity % 1 == 0f) item.quantity.toInt() else item.quantity}",
//                                    color = MaterialTheme.colorScheme.onSurface,
//                                    fontWeight = FontWeight.ExtraBold,
//                                    fontSize = 22.sp
//                                )
//                            }
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//        }
//        if (inventoryViewModel.showDialog) {
//            var confirmStatus: String = ""
//            Log.d("attempt to update", inventoryViewModel.selectedItem.toString())
//            CustomInventoryPopup(
//                showDialog = inventoryViewModel.showDialog,
//                onDismiss = { inventoryViewModel.closeDialog() },
//                onConfirm = {name, qty, type ->
//                    if (inventoryViewModel.selectFromNER.value) {
//                        confirmStatus =  inventoryViewModel.onConfirm(name, qty, type)
//                    }
//                    if (confirmStatus != "SUCCESS" && confirmStatus != "SIMILAR") {
//                        showAlert = true
//                    } else {
//                        inventoryViewModel.closeDialog()
//                    }
//                },
//                modifier = Modifier,
//                name = inventoryViewModel.selectedItem?.title.orEmpty(),
//                quantity = inventoryViewModel.selectedItem?.quantity ?: 0f,
//                type = inventoryViewModel.selectedItem?.unitType.orEmpty(),
//                status = inventoryViewModel.addOrUpdate,
//                selectNER = inventoryViewModel.selectFromNER,
//                onEdit = onEdit
//            )
//            if (showAlert) {
//                AlertForInvalidity { showAlert = false }
//            }
//        }
//
//    }

//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Inventory",
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 32.sp
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Transparent,
//                )
//            )
//        }
//    ) { innerPadding->
//
//
//    }

//}

@Composable
fun AlertForInvalidity(isGrocery: Boolean, onDismiss: () -> Unit){
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    text = "Oops!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if(!isGrocery) "Make sure you chose an ingredient form the list or entered a valid quantity"
                    else "Make sure you put valid name and quantity",
                    textAlign = TextAlign.Center,
                )
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(34),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Ok",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

        }
    }
}

