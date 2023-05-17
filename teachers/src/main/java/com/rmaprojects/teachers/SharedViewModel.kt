package com.rmaprojects.teachers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SharedViewModel: ViewModel() {
    private val _location = MutableLiveData<Location>()
    val location = _location

    fun setLocation(newLocation: Location) {
        _location.postValue(newLocation)
    }
}
data class Location(
    val longitude: Double,
    val latitude: Double
)