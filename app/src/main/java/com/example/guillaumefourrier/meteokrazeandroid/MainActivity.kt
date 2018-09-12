package com.example.guillaumefourrier.meteokrazeandroid

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

       val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
           // finish()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null) {
                getWeather(location.longitude, location.latitude)
            }
        }

        refresh()

        getWeather("Paris")
        getWeather("Nice")
        getWeather("New York")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val apiKey = "4271a4992f162462f555468b8aa580f2"

    fun getWeather(cityName: String) {
        val queue = Volley.newRequestQueue(this)

        val url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey
        requestAndgetData(queue, url)

    }

    fun getWeather(lon: Double, lat: Double) {
        val queue = Volley.newRequestQueue(this)

        val url = "http://api.openweathermap.org/data/2.5/weather?lat=" +
                lat + "&lon=" + lon + "&appid=" + apiKey
        requestAndgetData(queue, url)
    }

    fun refresh() {
        if (MeteoService.shared.cities.isEmpty()) {
            loading_text.visibility = View.VISIBLE
            city_list_view.visibility = View.GONE
        } else {
            loading_text.visibility = View.GONE
            city_list_view.visibility = View.VISIBLE
        }

        city_list_view.adapter = CityCardAdapter(this, MeteoService.shared.cities)
    }

    private fun requestAndgetData(queue: RequestQueue, url: String) {

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val klaxon = Klaxon()
                    val data = klaxon.parse<WeatherData>(response)

                    if (data != null) {
                        MeteoService.shared.cities.add(data)
                    }

                    runOnUiThread {
                        refresh()
                        loading_text.text = "Meteo a paname " + data?.main?.temp?.minus(273.15F)?.toInt() + "°C"
                    }
                },
                Response.ErrorListener {
                    runOnUiThread {
                        loading_text.text = it.toString()
                    }
                })

        queue.add(stringRequest)
    }

}
