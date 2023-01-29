package com.example.wise_batteryexam2023

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val text: String,
    val icon: ImageVector
) {
    object ChargeScreen : BottomBarScreen(
        route = "charge_screen",
        title = "ChargeScreen",
        text = "Current Charge",
        icon = Icons.Filled.Add
    )
    object DataScreen : BottomBarScreen(
        route = "data_screen",
        title = "DataScreen",
        text = "Data Overview",
        icon = Icons.Filled.Info
    )
    object LifeScreen : BottomBarScreen(
        route = "life_screen",
        title = "LifeScreen",
        text = "Life Expectancy",
        icon = Icons.Filled.Phone
    )
    object AppScreen : BottomBarScreen(
        route = "app_screen",
        title = "AppScreen",
        text = "Apps",
        icon = Icons.Filled.List
    )
}
