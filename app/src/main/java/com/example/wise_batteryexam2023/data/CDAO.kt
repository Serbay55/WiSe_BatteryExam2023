package com.example.wise_batteryexam2023.data

import androidx.room.*

@Dao
interface CDAO {
    @Insert
    suspend fun insertCharge(charge: Charge)

    @Query("SELECT id FROM charge_stats WHERE dayofyear = :askedday AND year = :askedyear")
    fun getTodaysChargeStatid(askedday: Int, askedyear: Int): Int

    @Query("SELECT chargeStep FROM charge_stats WHERE dayofyear = :askedday AND year = :askedyear")
    fun getTodaysChargeCycles(askedday: Int, askedyear: Int): Double

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

    @Query("SELECT chargeStep FROM charge_stats WHERE dayofyear= :dayyear AND year = :year ")
    fun getCycles(dayyear: Int, year: Int) : Double

    @Insert
    suspend fun insertHealth(health: BH)

    @Query("SELECT healthstateofbattery FROM current_health WHERE id = 0")
    fun getCurrentHealth(): Double
}