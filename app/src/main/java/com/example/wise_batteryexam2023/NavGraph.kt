package com.example.wise_batteryexam2023

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wise_batteryexam2023.ui.screens.AppScreen
import com.example.wise_batteryexam2023.ui.screens.ChargeScreen
import com.example.wise_batteryexam2023.ui.screens.DataScreen
import com.example.wise_batteryexam2023.ui.screens.LifeScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
){
    NavHost(
        navController = navController,
        startDestination = Screen.Charge.route
    ){
        composable(
            route = Screen.Charge.route
        ){
            ChargeScreen()
        }
        composable(
            route = Screen.Data.route
        ){
            DataScreen()
        }
        composable(
            route = Screen.Life.route
        ){
            LifeScreen()
        }
        composable(
            route = Screen.App.route
        ){
            AppScreen()
        }
    }
}
