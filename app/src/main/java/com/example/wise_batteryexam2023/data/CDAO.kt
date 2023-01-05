package com.example.wise_batteryexam2023.data

import androidx.room.*

@Dao
interface CDAO {
    @Insert
    suspend fun insertCharge(charge: Charge)

    @Query("SELECT * FROM charge_stats")
    fun getAllCharges(): List<Charge>

    @Update
    suspend fun updateCharge(charge: Charge)

    @Delete
    suspend fun deleteCharge(charge: Charge)
}