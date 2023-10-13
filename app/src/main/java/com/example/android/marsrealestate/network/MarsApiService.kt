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

package com.example.android.marsrealestate.network


import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://mars.udacity.com/"

/** Moshi Builder to create a Moshi object with the KotlinJsonAdapterFactory*/
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())//KotlinJsonAdapterFactory for moshi builder to work properly with Kt
    .build()

/**
 * retrofit builder w/ scalar converterFactory and BASE_URL
 * fetch JSON response and return it as string */
private val retrofit = Retrofit.Builder()
    //.addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi)) // convert JSON response into kotlin object
    .baseUrl(BASE_URL)
    .build()

/**
 * getProperties() method to request the JSON response string.*/
interface MarsApiService {
    @GET("realestate")
    fun getProperties():
            Call<List<MarsProperty>>
}

/**
 * object MarsApi to expose the Retrofit service to the rest of the app w/ MarsApiService*/
object MarsApi {
    val retrofitService : MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }
}