package com.example.wise_batteryexam2023.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Charge::class, LCS::class, BH::class, LH::class], version = 1)
abstract class DB : RoomDatabase(){
    abstract fun cDAO() : CDAO
}

