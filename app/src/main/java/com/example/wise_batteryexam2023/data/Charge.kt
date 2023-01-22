package com.example.wise_batteryexam2023.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charge_stats")
data class Charge(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var chargeStep: Double,
    @ColumnInfo
    var dayofyear: Int?,
    @ColumnInfo
    var year: Int?,
)

@Entity(tableName = "lastChargeStats")
data class LCS(
    @PrimaryKey(autoGenerate = true)
    var sid: Int,
    @ColumnInfo
    var lastChargeStatus: Int?

)
@Entity(tableName = "battery_health")
data class BH(
    @PrimaryKey(autoGenerate = false)
    var date: Int,
    @ColumnInfo
    var year: Int,
    @ColumnInfo
    var battery_health_state: Double
)
@Entity(tableName = "current_health")
data class LH(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var healthstateofbattery : Double

)
@Entity(tableName = "screen_on_time")
data class ScreenOTime(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var app_package_name : String,
    @ColumnInfo
    var SoT : Int,
    @ColumnInfo
    var usedPercentage: Double
)
@Entity(tableName = "last_running_app")
data class LRA(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var last_running_app: String
)
