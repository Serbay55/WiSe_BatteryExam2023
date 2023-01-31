package com.example.wise_batteryexam2023.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.wise_batteryexam2023.BottomBarScreen
import com.example.wise_batteryexam2023.SetupNavGraph
import com.example.wise_batteryexam2023.ui.theme.AlphaMax
import com.example.wise_batteryexam2023.ui.theme.BlueTertiary
import com.example.wise_batteryexam2023.ui.theme.BrightSecondary
import com.example.wise_batteryexam2023.ui.theme.DarkPrimary

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navController = navController)},
        containerColor = DarkPrimary
    ) {
      SetupNavGraph(navController = navController)
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.ChargeScreen,
        BottomBarScreen.DataScreen,
        BottomBarScreen.LifeScreen,
        BottomBarScreen.AppScreen
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(
        backgroundColor = DarkPrimary
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
){
    BottomNavigationItem(
        label = {
            Text(
                text = screen.title,
                color = BrightSecondary,
                fontSize = 14.sp
            )
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "NavigationIcon",
                //make Icons invisible
                tint = AlphaMax,
            )
        },
        selected = currentDestination?.hierarchy?.any{
            it.route == screen.route
        } == true,
        selectedContentColor = BlueTertiary,
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
                navController.navigate(screen.route){
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun MainScreenPreview () {
    MainScreen()
}