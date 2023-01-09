package com.example.wise_batteryexam2023.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lastChargeStats")
data class LastCharge (
    @PrimaryKey(autoGenerate = true)
    var sid: Int?,
    @ColumnInfo
    var lastChargeStatus: Int?
    )
