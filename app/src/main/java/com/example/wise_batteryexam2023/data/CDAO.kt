package com.example.wise_batteryexam2023.data

import androidx.room.*

@Dao
interface CDAO {
    @Insert
    suspend fun insertCharge(charge: Charge)

    @Query("SELECT id FROM charge_stats WHERE dayofyear = :askedday")
    fun getTodaysChargeStatid(askedday: Int): Int

    @Insert
    suspend fun insertLCS(lcs: LCS)

    @Query("SELECT * FROM charge_stats")
    fun getAllCharges(): List<Charge>

    @Update
    suspend fun updateCharge(charge: Charge)

    @Delete
    suspend fun deleteCharge(charge: Charge)

    @Query("SELECT lastChargeStatus FROM lastChargeStats WHERE sid = 0")
    fun getLastCharge(): Int

    @Delete
    suspend fun deleteLastCharge(ls: LCS)
}