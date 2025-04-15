package com.example.late_plate.ui.screens.home
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.R
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.dummy.dummyRecipes
import com.example.late_plate.network.RecipeResponse
import com.example.late_plate.ui.components.FilterationBar
import com.example.late_plate.ui.components.RecipeCard
import com.example.late_plate.ui.components.RecommendationCard
import com.example.late_plate.ui.theme.Late_plateTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier,data:List<RecipeResponse>) {
    Column ( modifier= modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(horizontal = 16.dp)){
        Text(
            stringResource(R.string.welcome_text),
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onPrimary

        )
        Text(
            "\"no need to worry about what you eat\"",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp
        )
       // RecipeCard(recipe =  data.first(), onClick = {})
        Spacer(modifier=Modifier.height(24.dp))

        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Text(
                "Personalized Picks",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Row {
                Text(
                    stringResource(R.string.see_all),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }

        }
        RecommendationCarousel(recipes = data, onClick = {})
        Spacer(modifier=Modifier.height(15.dp))
        Row(
            modifier=Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(R.string.meals),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector =Icons.Default.Tune,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier=Modifier.height(16.dp))
        FilterationBar()
        PopularPicks(recipes = data)
    }
}

@Composable
fun RecommendationCarousel(recipes:List<RecipeResponse>, onClick: (RecipeResponse) -> Unit){
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(156.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
       horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(recipes) { item ->
            Box(modifier = Modifier.fillParentMaxWidth()) {  // Ensures it takes full screen width
                RecommendationCard(recipe = item, onClick = { onClick(item) })
            }

        }

    }

}
@Composable
fun PopularPicks(recipes: List<RecipeResponse>){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier =  Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) { 
        items(recipes){item->
            RecipeCard(recipe = item, onClick = {})
        }
    }
}

