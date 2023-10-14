/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties


    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    /**creating coroutine job and coroutineScope using main dispatcher*/
    private var viewModelJob = Job()

    /**Dispatchers.Main uses the UI thread since retrofit does all its work in the BG thread */
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )
    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     ** MarsApi.retrofitService to enqueue the Retrofit request in getMarsRealEstateProperties(),
     ** overriding the required Retrofit callbacks to assign the JSON response
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {

        // coroutines manage concurrency
        coroutineScope.launch {

            // call enqueue on the call back to start the network request on a bg thread
            // call getProperties from MarsApiService creates+starts the network call in bg thread & return the deferred
            var getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
            // calling await on defered return result from network call when the values are ready w/o blocking the current thread
            try {

                var listResult = getPropertiesDeferred.await()

                if (listResult.size > 0) {

                    _properties.value = listResult
                }
               // _response.value = "Success: ${listResult.size} Mars properties retrieved"
            }catch (e: Exception){

                _status.value = MarsApiStatus.ERROR
                //In the error case, clear the properties LiveData by setting it to a new empty ArrayList
                _properties.value = ArrayList()
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        // cancel job after finishing its task
        viewModelJob.cancel()
    }

    /** initiate navigation to the detail screen **/
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }

    /** set _navigateToSelectedProperty to false once navigation is completed to prevent unwanted extra navigations **/
    fun displayPropertyDetailsComplete() { _navigateToSelectedProperty.value = null }

    /** to requery the data by calling getMarsRealEstateProperties with the new filter*/
    fun updateFilter(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }
}

enum class MarsApiStatus { LOADING, ERROR, DONE }