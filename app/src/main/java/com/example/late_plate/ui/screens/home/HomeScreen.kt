package com.example.late_plate.ui.screens.home

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.late_plate.R
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.FilterationBar
import com.example.late_plate.ui.components.RecipeCard
import com.example.late_plate.ui.components.RecommendationCard
import com.example.late_plate.ui.screens.FABState
import com.example.late_plate.ui.screens.HomeRecipeRoute

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    data: List<Recipe>,
    fabState: FABState,
    navController: NavHostController
) {
    fabState.changeFAB(newIcon = Icons.Outlined.Person, newOnClick = {})
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()

    ) {
        Column(modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentHeight()
            .fillMaxWidth()) {
            Text(
                stringResource(R.string.welcome_text),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold

            )
            Text(
                "\"no need to worry about what you eat\"",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )
            // RecipeCard(recipe =  data.first(), onClick = {})
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text(
                    "Personalized Picks",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.see_all),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        RecommendationCarousel(recipes = data, navController)
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Meals",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

            }
            FilterationBar()
            Spacer(modifier = Modifier.height(16.dp))
            PopularPicks(recipes = data,navController)
        }
    }
}

@Composable
fun RecommendationCarousel(recipes: List<Recipe>, navController: NavController) {
    val lazyListState = rememberLazyListState()
    var parentWidth by remember { mutableStateOf(0) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp)
            .onSizeChanged { parentWidth = it.width },
        state = lazyListState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),

        ) {
        items(recipes) { recipe ->
            RecommendationCard(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp),
                recipe = recipe,
                onClick = { navController.navigate(HomeRecipeRoute(recipe) )})

        }
    }

    LaunchedEffect(recipes.size) {
        if (recipes.isNotEmpty()) {
            lazyListState.animateScrollToItem(0)
        }
    }
}


@Composable
fun PopularPicks(recipes: List<Recipe>,navController:NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipe = recipe, onClick = {navController.navigate(HomeRecipeRoute(recipe))})
        }
        item {
            Spacer(modifier = Modifier.height(86.dp))
        }
    }
}

