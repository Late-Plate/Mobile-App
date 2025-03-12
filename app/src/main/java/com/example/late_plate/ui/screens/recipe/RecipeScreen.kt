package com.example.late_plate.ui.screens.recipe

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.late_plate.dummy.Recipe
import com.example.late_plate.ui.components.CustomCard
import com.example.late_plate.ui.components.OnlineImageCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(modifier: Modifier = Modifier, data: Recipe) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(data.title, color = MaterialTheme.colorScheme.onPrimary) },
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
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            )
        )
        OnlineImageCard(
            url = data.imageUrl, modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
        )

        CustomCard(modifier = Modifier.offset(y = (-24).dp), padding = 8) {
            Row(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row {

                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null, tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(data.time, color = MaterialTheme.colorScheme.onPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row {
                    Icon(
                        imageVector = Icons.Outlined.Speed,
                        contentDescription = null, tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(data.difficulty, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
        Column (modifier=Modifier.fillMaxWidth().padding(horizontal = 16.dp)){
            CustomCard (modifier = Modifier.fillMaxWidth()){



            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomCard (modifier = Modifier.fillMaxWidth()){


            }
            Spacer(modifier = Modifier.height(16.dp))
            CustomCard (modifier = Modifier.fillMaxWidth()){


            }
        }


    }
}

