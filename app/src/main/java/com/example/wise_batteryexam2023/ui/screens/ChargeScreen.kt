package com.example.wise_batteryexam2023.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wise_batteryexam2023.data.*
import com.example.wise_batteryexam2023.ui.screens.ChargeScreen
import com.example.wise_batteryexam2023.ui.theme.*
import java.util.*

@Composable
fun ChargeScreen(){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ){
        Column {
            ChargeTopText()
            CircularChargeBar(percentage = 0.8f, number =100 )
        }
    }
}

@Composable
fun ChargeTopText(
    TopText: String = "Your phones' current charge is:",
    fontSize: TextUnit = 21.sp
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

//Circular Bar

@SuppressLint("RememberReturnType")
@Composable
fun CircularChargeBar(
    percentage : Float,
    number : Int,
    fontSize: TextUnit = 100.sp,
    radius: Dp = 200.dp,
    color: Color = BlueTertiary,
    strokeWidth: Dp = 5.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
){
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val curPercentage = animateFloatAsState(
        targetValue = if(animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )
    LaunchedEffect(key1 = true){
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.
                size(radius * 2f).
                padding(50.dp)
    ){
        Canvas(modifier = Modifier.size(radius * 2f)){
            drawArc(
                color = color,
                -90f,
                360 * curPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Butt )
            )
        }
        Text(
            text = (curPercentage.value * number).toInt().toString() + "%",
            color = BrightSecondary,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ChargeScreenPreview () {
    ChargeScreen()
}