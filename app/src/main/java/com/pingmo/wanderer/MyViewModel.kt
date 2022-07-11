package com.pingmo.wanderer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel(){
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

    fun click() {
        _money.value = _money.value?.plus(1)
    }
}