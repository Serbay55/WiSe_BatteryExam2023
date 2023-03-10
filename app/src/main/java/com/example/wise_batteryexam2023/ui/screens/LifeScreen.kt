package com.example.wise_batteryexam2023.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wise_batteryexam2023.MainActivity
import com.example.wise_batteryexam2023.methods.StandardMethods
import com.example.wise_batteryexam2023.ui.MainViewModel
import com.example.wise_batteryexam2023.ui.theme.BlueTertiary
import com.example.wise_batteryexam2023.ui.theme.BrightSecondary

@Composable
fun LifeScreen(viewmodel: MainViewModel){

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Column {
            TopText()
            MiddleText(lifeExpectancy = 420/*StandardMethods(MainActivity().receiveContext()).getBattery()*/)
            InformationText()
        }
    }
}
@Composable
fun TopText(
    TopText: String = "Your phones' estimated remaining batterylife is:",
    fontSize: TextUnit = 25.sp
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp)
    ){
        Text(
            text = TopText,
            color = BrightSecondary,
            fontSize = fontSize
        )
    }
}

@Composable
fun MiddleText(
    lifeExpectancy : Int,
    ageText: String = " days",
    fontSize: TextUnit = 75.sp
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(20.dp)
    ){
        Text(
            text = "$lifeExpectancy" + ageText,
            style = TextStyle(textDecoration = TextDecoration.Underline),
            color = BlueTertiary,
            fontSize = fontSize
        )
    }
}

@Composable
fun InformationText(
    infoText: String = "Estimate is based on measured temperatures of your device, age of your device and charging behaviour." +
            "You may increase the life expectancy of your device by reducing power consumption and, consequently, lowering the frequency of charing cycles",
    fontSize: TextUnit = 15.sp
){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp)
    ){
        Text(
            text = infoText,
            color = BrightSecondary,
            fontSize = fontSize
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LifeScreenPreview () {
    //LifeScreen()
}