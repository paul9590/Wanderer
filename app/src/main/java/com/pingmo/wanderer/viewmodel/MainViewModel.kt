package com.pingmo.wanderer.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pingmo.wanderer.activitiy.*

class MainViewModel : ViewModel(){
    private val _name = MutableLiveData<String>()
    private val _money = MutableLiveData<Int>()

    init {
        _name.value = "paul"
        _money.value = 0
    }

    val name : LiveData<String>
        get() = _name

    val money : LiveData<Int>
        get() = _money

}