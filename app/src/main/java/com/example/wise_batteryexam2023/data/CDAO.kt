package com.example.wise_batteryexam2023.data

import androidx.room.*

@Dao
interface CDAO {
    //Charge Queries

    @Insert
    suspend fun insertCharge(charge: Charge)

    @Query("SELECT dayofyear FROM charge_stats WHERE id = :sid")
    fun getdayofyearcharge(sid: Int): Int

    @Query("SELECT id FROM charge_stats WHERE dayofyear = :askedday AND year = :askedyear")
    fun getTodaysChargeStatid(askedday: Int, askedyear: Int): Int

    @Query("SELECT chargeStep FROM charge_stats WHERE dayofyear = :askedday AND year = :askedyear")
    fun getTodaysChargeCycles(askedday: Int, askedyear: Int): Double

    @Query("SELECT * FROM charge_stats")
    fun getAllCharges(): List<Charge>

    @Query("SELECT chargeStep FROM charge_stats WHERE dayofyear= :dayyear AND year = :year ")
    fun getCycles(dayyear: Int, year: Int) : Double

    @Update
    suspend fun updateCharge(charge: Charge)

    @Delete
    suspend fun deleteCharge(charge: Charge)

    //___________________________________________________________________________
    //Last Charge Stats Queries


    @Insert
    suspend fun insertLCS(lcs: LCS)

    @Update
    suspend fun updateLCS(lcs: LCS)

    @Query("SELECT lastChargeStatus FROM lastChargeStats WHERE sid = :chosenid")
    suspend fun tester(chosenid: Int): Int

    @Query("SELECT * FROM lastChargeStats")
    fun getAllLastCharges(): List<LCS>

    @Query("SELECT COUNT(1) FROM lastChargeStats WHERE sid = :askedid")
    fun checkExistingLC(askedid: Int): Int

    @Query("SELECT lastChargeStatus FROM lastChargeStats WHERE sid = 1")
    fun getLastCharge(): Int

    @Delete
    suspend fun deleteLastCharge(ls: LCS)

    //____________________________________________________________________________
    //Screen On Time Queries

    @Query("SELECT id FROM screen_on_time WHERE dayofyear > :askedday AND year = :askedyear")
    fun getnewestID(askedday: Int, askedyear: Int): Int

    @Query("SELECT time FROM screen_on_time WHERE dayofyear = :askedday AND year = :askedyear")
    fun gettimefromtoday(askedyear: Int, askedday: Int): Int


    @Query("SELECT * FROM screen_on_time")
    fun getAllSOT(): List<ScreenOTime>

    @Query("SELECT COUNT(1) FROM screen_on_time WHERE dayofyear = :askedday AND year = :askedyear")
    fun checkExistingSOT(askedday: Int,askedyear: Int): Int

    @Query("SELECT id FROM screen_on_time WHERE dayofyear = :askedday AND year = :askedyear")
    fun getIDSOT(askedday: Int, askedyear: Int): Int

    @Update
    fun updateSOT(sot: ScreenOTime)

    @Insert
    suspend fun insertScreenOnTime(sot: ScreenOTime)

    //______________________________________________________________________________________
    //Final Charge Stats Queries

    @Query("SELECT COUNT(1) FROM final_charge_stats WHERE id = :askedid")
    fun checkExistingFCS(askedid: Int): Int

    @Insert
    fun insertFCS(fcs: FCS)

    @Update
    fun updateFCS(fcs: FCS)

    @Query("SELECT finalchargecycles FROM final_charge_stats WHERE id = :askedid")
    fun getFCS(askedid: Int): Double

    //_______________________________________________________________________________________
    //Battery Health state Queries

    @Insert
    suspend fun insertHealth(health: LH)

    @Update
    suspend fun updateHealth(health: LH)

    @Query("SELECT healthstateofbattery FROM current_health WHERE id = 1")
    fun getCurrentHealth(): Double

    @Query("SELECT COUNT(1) FROM current_health WHERE id = 1")
    fun checkexistinghealth(): Int


}