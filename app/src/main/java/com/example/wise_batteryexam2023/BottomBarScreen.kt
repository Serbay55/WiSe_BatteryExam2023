package com.example.wise_batteryexam2023

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector
//require Icons for BottomBarItem function
sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val text: String,
    val icon: ImageVector
) {
    object ChargeScreen : BottomBarScreen(
        route = "charge_screen",
        title = "Current Charge",
        text = "Current Charge",
        icon = Icons.Filled.Add
    )
    object DataScreen : BottomBarScreen(
        route = "data_screen",
        title = "Data Overview",
        text = "Data Overview",
        icon = Icons.Filled.Info
    )
    object LifeScreen : BottomBarScreen(
        route = "life_screen",
        title = "Life Expectancy",
        text = "Life Expectancy",
        icon = Icons.Filled.Phone
    )
    object AppScreen : BottomBarScreen(
        route = "app_screen",
        title = "App rankings",
        text = "Apps",
        icon = Icons.Filled.List
    )
}
