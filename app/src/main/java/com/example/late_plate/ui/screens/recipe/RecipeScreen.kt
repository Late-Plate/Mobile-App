package com.example.late_plate.ui.screens.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.R
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.ui.components.CustomButton
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.ExpandableCard
import com.example.late_plate.ui.components.OnlineImageCard
import com.example.late_plate.viewModel.InventoryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    modifier: Modifier,
    recipe: Recipe,
    inventoryViewModel: InventoryViewModel
) {

    var showAlert by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(recipe.title, color = MaterialTheme.colorScheme.onPrimary) },
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Back"
                    )
                }

            },
            actions = {
                Row(){
                    IconButton(onClick = {showAlert = true}) {
                        Icon(
                            painter = painterResource(R.drawable.shopping_cart_icon),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { saved = !saved }) {
                        Icon(
                            imageVector = if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = if (saved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            )
        )
        OnlineImageCard(
            imageUrl = recipe.imageUrl, modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
        )

        CustomCard(modifier = Modifier.offset(y = (-24).dp), contentPadding = 8) {
            Row(modifier = Modifier.padding(horizontal = 24.dp)) {

                Icon(
                    imageVector = Icons.Outlined.Timer,
                    contentDescription = null, tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(recipe.time, color = MaterialTheme.colorScheme.onPrimary)

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Outlined.Speed,
                    contentDescription = null, tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(recipe.difficulty, color = MaterialTheme.colorScheme.onPrimary)

            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .verticalScroll(
                    rememberScrollState()
                ), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExpandableCard(
                title = "description",
                content = recipe.description,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ExpandableCard(
                title = "ingredients",
                content = recipe.ingredients.toBulletList(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ExpandableCard(
                title = "instructions",
                content = recipe.directions.toBulletList(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(86.dp))
        }


    }

    if(showAlert){
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            RecipePopup(
                onOk = {
                    inventoryViewModel.addRecipeIngredientsToGroceryList(recipe.ingredients)
                    showAlert = false
                },
                onCancel = { showAlert = false }
            )
        }
    }

}


fun List<String>.toBulletList(): String {
    return joinToString("\n") { "• $it" }
}


@Composable
fun RecipePopup(
    onOk: ()-> Unit,
    onCancel: ()-> Unit
){
    Card(
        modifier= Modifier
            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.cart_add_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(75.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Add missing ingredients to Grocery List?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(horizontal = 32.dp)
                    .background(MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {onOk()},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Ok"
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {onCancel()},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.cancel_btn_color),
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }




    }
}
