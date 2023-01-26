package com.example.wise_batteryexam2023

sealed class Screen(val route: String) {
    object Charge: Screen(route = "charge_screen")
    object Data: Screen(route = "sata_screen")
    object Life: Screen(route = "life_screen")
    object App: Screen(route = "app_screen")
}
