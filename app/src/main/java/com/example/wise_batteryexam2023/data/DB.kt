package com.example.wise_batteryexam2023.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Charge::class, LCS::class, BH::class, LH::class, ScreenOTime::class, LRA::class, FCS::class, NCC::class], version = 1)
abstract class DB : RoomDatabase(){
    abstract fun cDAO() : CDAO

    companion object{
        private var INSTANCE: DB? = null

        fun getInstance(context: Context) : DB? {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DB::class.java,
                        "net_charge_capacity"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }

        }
    }
}

