package com.example.late_plate.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.late_plate.ui.theme.Late_plateTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.late_plate.network.RecipeGenerationClient
import kotlinx.coroutines.launch
import util.onError
import util.onSuccess

@Composable
fun App(client: RecipeGenerationClient){
    Late_plateTheme {
        var text by remember {mutableStateOf("")}
        var recipe by remember {mutableStateOf("your recipe will appear here")}
        var isLoading by remember { mutableStateOf(false) }
        val scope=rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ){
            TextField(
                modifier = Modifier.padding(16.dp),
                value = text,
                onValueChange = {text=it},
                label = {Text("Enter a prompt")}
            )
            Button(onClick = {
                scope.launch {
                    isLoading=true
                    client.generateRecipe(prompt = text).onSuccess {
                        recipe=it
                    }
                        .onError {
                            recipe= it.toString()
                        }
                    isLoading=false
                }

            }) {

                if(isLoading){
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                else{
                    Text(text = "Click me")
                }
            }
            Text(text = recipe,
                fontSize = 20.sp
            )
        }
    }
}