package com.example.late_plate.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.late_plate.R
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.RecipeCard
import com.example.late_plate.ui.components.RecommendationCard
import com.example.late_plate.ui.screens.FABState
import com.example.late_plate.ui.screens.HomeRecipeRoute
import com.example.late_plate.viewModel.RecipeCatalogViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    data: List<Recipe>,
    fabState: FABState,
    navController: NavHostController,
    recipeCatalogViewModel: RecipeCatalogViewModel
) {
    val catalogRecipes by recipeCatalogViewModel.recipes.collectAsState()
    var showSearch by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    fabState.changeFAB(newIcon = Icons.Rounded.Person, newOnClick = {})

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.welcome_text),
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                "\"no need to worry about what you eat\"",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Personalized Picks",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        RecommendationCarousel(recipes = data, navController)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Meals",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )

                AnimatedVisibility(
                    visible = showSearch,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search meals...") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close search",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable { showSearch = false }
                                    .padding(4.dp)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(50))
                            .defaultMinSize(minHeight = 56.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }


                IconButton(onClick = { showSearch = !showSearch }) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Toggle Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }


            }

            Spacer(modifier = Modifier.height(8.dp))


            PopularPicks(
                navController = navController,
                recipeCatalogViewModel = recipeCatalogViewModel,
                query = query,
                showSearch
            )
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
        items(recipes.toList()) { recipe ->
            RecommendationCard(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(horizontal = 16.dp),
                recipe = recipe,
                onClick = { navController.navigate(HomeRecipeRoute(recipe)) }
            )
        }
    }

    LaunchedEffect(recipes.size) {
        if (recipes.isNotEmpty()) {
            lazyListState.animateScrollToItem(0)
        }
    }
}

@Composable
fun PopularPicks(
    navController: NavController,
    recipeCatalogViewModel: RecipeCatalogViewModel,
    query: String,
    showSearch: Boolean
) {
    val isSearching = query.isNotBlank()
    val recipeSet by if (isSearching&&showSearch)
        recipeCatalogViewModel.searchedRecipes.collectAsState()
    else
        recipeCatalogViewModel.recipes.collectAsState()

    val recipes = recipeSet.toList()
    val isLoading by recipeCatalogViewModel.isLoading.collectAsState()

    val listState = rememberLazyGridState()
    val page = rememberSaveable { mutableStateOf(0) }
    val pageSize = 10
    val searchPage=rememberSaveable { mutableStateOf(0) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipe = recipe, onClick = {
                navController.navigate(HomeRecipeRoute(recipe))
            })
        }

        if (isLoading ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(86.dp))
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            (lastVisible?.index ?: 0) >= totalItems - 1
        }
    }

    LaunchedEffect(shouldLoadMore.value, isLoading, query) {
        if (shouldLoadMore.value && !isLoading) {
            if(!isSearching){
                page.value++
                recipeCatalogViewModel.getRecipes(page.value, pageSize)
                searchPage.value=0
            }
            else{
                searchPage.value++
                recipeCatalogViewModel.searchRecipes(query,searchPage.value,pageSize)
            }

        }
    }

    LaunchedEffect(query) {
        if (isSearching) {
            recipeCatalogViewModel.searchRecipes(query)
        }
    }
}
