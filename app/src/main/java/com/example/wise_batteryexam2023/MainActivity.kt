package com.example.wise_batteryexam2023

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.wise_batteryexam2023.data.BatteryState
import com.example.wise_batteryexam2023.data.CDAO
import com.example.wise_batteryexam2023.data.Charge
import com.example.wise_batteryexam2023.data.DB
import com.example.wise_batteryexam2023.ui.main.SectionsPagerAdapter
import com.example.wise_batteryexam2023.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.concurrent.schedule
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    /*private lateinit var cDao: CDAO
    private lateinit var bstate : BatteryState*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val db = Room.databaseBuilder(
            applicationContext,
            DB::class.java, "charge_stats"
        ).build()

        cDao = db.cDAO()
        testDB()

         */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        /*Timer().schedule(1000){
            testTime()
        }*/
    }

    /*private fun testDB(){
        lifecycleScope.launch(Dispatchers.IO){
            //Insert
            Log.i("MyTAG","***** Inserting 3 stats ********")
            cDao.insertCharge(Charge(0,1))
            Log.i("MyTAG","***** Inserted 3 stats ********")

            //Query
            val stats = cDao.getAllCharges()
            for(x in stats){
                Log.i("MyTAG","id: ${x.id} charges: ${x.chargeStep}")
            }
        }
    }
    fun testTime(){
        bstate = BatteryState()
        Log.i("MyTAG: ",""+bstate.getBatteryPercentage(this).toInt())
    }*/
}