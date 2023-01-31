package com.example.wise_batteryexam2023.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wise_batteryexam2023.data.CDAO
import com.example.wise_batteryexam2023.data.Charge
import com.example.wise_batteryexam2023.data.NCC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class ChargeRepository(private val chargeDAO: CDAO) {
    val searchResults = MutableLiveData<List<Charge>>()
    val allNCC: LiveData<List<NCC>> = chargeDAO.getAll()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertNCC(ncc: NCC){
        coroutineScope.launch(Dispatchers.IO){
            chargeDAO.insertNCC(ncc)
        }
    }

    fun updateNCC(ncc: NCC){
        coroutineScope.launch(Dispatchers.IO){
            chargeDAO.updateNCC(ncc)
        }
    }

    fun getNCC(id: Int){
        coroutineScope.launch(Dispatchers.IO){
            chargeDAO.getNCC(id)
        }
    }

    fun findNCC(id: Int){
        coroutineScope.launch(Dispatchers.IO){
            chargeDAO.findNCC(id)
        }
    }

    fun getAll(){
        Timer().schedule(5000) {
            coroutineScope.launch(Dispatchers.IO) {
                chargeDAO.getAll()
                getAll()
            }
        }
    }


}