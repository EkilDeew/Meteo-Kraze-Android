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

    var cities: ArrayList<WeatherData> = ArrayList()


}