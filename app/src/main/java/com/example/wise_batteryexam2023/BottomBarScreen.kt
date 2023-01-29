package com.example.wise_batteryexam2023

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val text: String
) {
    object ChargeScreen : BottomBarScreen(
        route = "charge_screen",
        title = "ChargeScreen",
        text = "Current Charge",
    )
    object DataScreen : BottomBarScreen(
        route = "data_screen",
        title = "DataScreen",
        text = "Data Overview",
    )
    object LifeScreen : BottomBarScreen(
        route = "life_screen",
        title = "LifeScreen",
        text = "Life Expectancy",
    )
    object AppScreen : BottomBarScreen(
        route = "app_screen",
        title = "AppScreen",
        text = "Apps",
    )
}
