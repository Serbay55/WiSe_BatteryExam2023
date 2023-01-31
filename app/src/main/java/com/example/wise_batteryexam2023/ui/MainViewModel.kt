package com.example.wise_batteryexam2023.ui

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wise_batteryexam2023.data.Charge
import com.example.wise_batteryexam2023.data.DB
import com.example.wise_batteryexam2023.data.NCC
import com.example.wise_batteryexam2023.repository.ChargeRepository

class MainViewModel(application: Application): ViewModel() {

    val allNCC: LiveData<List<NCC>>
    private val repo: ChargeRepository
    val searchResults: MutableLiveData<List<Charge>>

    init {
        val chargeDb = DB.getInstance(application)
        val chargeDAO = chargeDb!!.cDAO()
        repo = ChargeRepository(chargeDAO)
        allNCC = repo.allNCC
        searchResults = repo.searchResults
    }

    fun insertNCC(ncc: NCC){
        repo.insertNCC(ncc)
    }

    fun updateNCC(ncc: NCC){
        repo.updateNCC(ncc)
    }

    fun getNCC(id: Int){
        repo.getNCC(id)
    }

    fun findNCC(id: Int){
        repo.findNCC(id)
    }

    fun getAll(){
        repo.getAll()
    }
}