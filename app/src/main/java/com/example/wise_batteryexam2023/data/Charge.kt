package com.example.wise_batteryexam2023.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charge_stats")
data class Charge(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var chargeStep: Int,
)
