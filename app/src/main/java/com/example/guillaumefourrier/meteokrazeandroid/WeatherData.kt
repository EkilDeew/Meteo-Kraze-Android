package com.example.guillaumefourrier.meteokrazeandroid

import com.beust.klaxon.Json
import java.io.Serializable
import java.util.*

data class WeatherData(
                val coord: Coord,
                val weather: ArrayList<Weather>,
                val main: MainW,
                val wind: Wind,
                val sys: Sys,
                val name: String) {
    var isCurr: Boolean = false
}

data class Coord(val lon: Float,
                val lat: Float)

data class Sys(val country: String,
               val sunrise: Long,
               val sunset: Long)

data class Weather(val id: Int,
                   val main: String,
                   val description: String,
                   val icon: String)

data class MainW(val temp: Float,
                 val humidity: Int,
                 val pressure: Int,
                 val temp_max: Float,
                 val temp_min: Float)

data class Wind(val speed: Float)

data class Rain(val threeHoursProb: Int)

data class Cloud(val all: Int)

