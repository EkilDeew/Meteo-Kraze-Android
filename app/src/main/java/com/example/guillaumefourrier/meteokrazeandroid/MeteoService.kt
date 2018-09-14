package com.example.guillaumefourrier.meteokrazeandroid

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.JsonObject
import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import org.json.JSONObject
import java.io.StringReader

public class MeteoService {

    private object Holder { val INSTANCE = MeteoService() }

    companion object {
        val shared: MeteoService by lazy { Holder.INSTANCE }
    }

    val SHARED_PREF_NAME = "meteo_kraze_pref"
    val SHARED_PREF_KEY = "meteo_kraze_city"

    var cities: ArrayList<WeatherData> = ArrayList()

    val PICK_CITY_REQUEST = 1

    fun getCityNames() : HashSet<String> {
        val cities_name = HashSet<String>()
        for (city in cities) {
            if (!city.isCurr) {
                cities_name.add(city.name)
            }
        }
        return cities_name
    }


}