package com.example.late_plate.data

import androidx.compose.runtime.mutableStateOf

object User {
    var username = mutableStateOf("")
    var email: String = ""
    var userID: Long = 0
}