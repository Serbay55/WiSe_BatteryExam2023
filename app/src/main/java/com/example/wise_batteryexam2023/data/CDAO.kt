package com.example.wise_batteryexam2023.data

import androidx.room.*

@Dao
interface CDAO {
    @Insert
    suspend fun insertCharge(charge: Charge)

    @Insert
    suspend fun insertScreenOnTime(sot: ScreenOTime)

    @Insert
    suspend fun insertLCS(lcs: LCS)

    @Update
    suspend fun updateLCS(lcs: LCS)

    @Query("SELECT lastChargeStatus FROM lastChargeStats WHERE sid = :chosenid")
    suspend fun tester(chosenid: Int): Int

    @Query("SELECT dayofyear FROM charge_stats WHERE id = :sid")
    fun getdayofyearcharge(sid: Int): Int

    @Query("SELECT id FROM screen_on_time WHERE dayofyear > :askedday AND year = :askedyear")
    fun getnewestID(askedday: Int, askedyear: Int): Int

    @Query("SELECT time FROM screen_on_time WHERE dayofyear = :askedday AND year = :askedyear")
    fun gettimefromtoday(askedyear: Int, askedday: Int): Int

    @Query("SELECT id FROM charge_stats WHERE dayofyear = :askedday AND year = :askedyear")
    fun getTodaysChargeStatid(askedday: Int, askedyear: Int): Int

    @Query("SELECT chargeStep FROM charge_stats WHERE dayofyear = :askedday AND year = :askedyear")
    fun getTodaysChargeCycles(askedday: Int, askedyear: Int): Double


    @Query("SELECT * FROM charge_stats")
    fun getAllCharges(): List<Charge>

    @Query("SELECT * FROM lastChargeStats")
    fun getAllLastCharges(): List<LCS>

    @Query("SELECT * FROM screen_on_time")
    fun getAllSOT(): List<ScreenOTime>

    @Query("SELECT COUNT(1) FROM screen_on_time WHERE dayofyear = :askedday AND year = :askedyear")
    fun checkExistingSOT(askedday: Int,askedyear: Int): Int

    @Query("SELECT id FROM screen_on_time WHERE dayofyear = :askedday AND year = :askedyear")
    fun getIDSOT(askedday: Int, askedyear: Int): Int

    @Update
    fun updateSOT(sot: ScreenOTime)

    @Update
    suspend fun updateCharge(charge: Charge)

    @Delete
    suspend fun deleteCharge(charge: Charge)

    @Query("SELECT lastChargeStatus FROM lastChargeStats WHERE sid = 1")
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