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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch
import androidx.compose.ui.res.colorResource

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    modifier: Modifier = Modifier,
    onEdit: (String) -> List<String>,
    pagerState: PagerState,
    fabState: FABState
) {
    val tabTitles = listOf("Inventory", "Grocery List")
    val coroutineScope = rememberCoroutineScope()
    var showAlert by remember { mutableStateOf(false) }
    fabState.run {
        changeFAB(newIcon = Icons.Rounded.Add, newOnClick = {

            inventoryViewModel.addOrUpdate = InventoryPopUpState.ADD
            if(pagerState.currentPage==0){
                inventoryViewModel.openInventoryDialog()
            }
            else{
                inventoryViewModel.openGroceryDialog()
            }
        })
    }

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


    Column (
        modifier = modifier

    ){
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


@Composable
fun AlertForInvalidity(isGrocery: Boolean, onDismiss: () -> Unit){
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

