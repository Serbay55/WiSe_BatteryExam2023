package com.example.wise_batteryexam2023

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wise_batteryexam2023.ui.main.ChargeScreen

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
