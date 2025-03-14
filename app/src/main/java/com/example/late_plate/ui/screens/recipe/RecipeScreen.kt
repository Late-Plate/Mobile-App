package com.example.late_plate.ui.screens.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.ExpandableCard
import com.example.late_plate.ui.components.OnlineImageCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(modifier: Modifier, recipe: Recipe) {
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
                IconButton(onClick = { saved = !saved }) {
                    Icon(
                        imageVector = if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = if (saved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    )
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
                content = recipe.steps.toBulletList(),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(86.dp))
        }


    }

}

fun List<String>.toBulletList(): String {
    return joinToString("\n") { "• $it" }
}

