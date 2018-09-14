package com.example.guillaumefourrier.meteokrazeandroid

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
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
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import java.lang.Thread.sleep

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val apiKey = "4271a4992f162462f555468b8aa580f2"

    val PREMISSION_LOCATION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
           val intent = Intent(this, AddCity::class.java)
            startActivityForResult(intent, MeteoService.shared.PICK_CITY_REQUEST)
        }

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        PREMISSION_LOCATION_REQUEST)
            }
        } else {
            getLocationWeather()
        }

        val sharedPref = this.getSharedPreferences(
                MeteoService.shared.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val cities = sharedPref.getStringSet(resources.getString(R.string.shared_pref_key), null)
        if (cities != null) {
            for (city in cities) {
                getWeather(city)
            }
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MeteoService.shared.PICK_CITY_REQUEST -> {
                super.onActivityResult(requestCode, resultCode, data)
                if (resultCode == RESULT_OK && data != null) {
                    val city = data.getStringExtra("city")
                    Log.d("Guillaume", "Got result intent for city : " + city)
                    if (city != null) {
                        getWeather(city)
                    }

                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun getWeather(cityName: String) {
        val queue = Volley.newRequestQueue(this)

        val url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey
        requestAndgetData(queue, url, false)

    }

    fun getWeather(lon: Double, lat: Double) {
        val queue = Volley.newRequestQueue(this)

        val url = "http://api.openweathermap.org/data/2.5/weather?lat=" +
                lat + "&lon=" + lon + "&appid=" + apiKey
        requestAndgetData(queue, url, true)
    }

    fun refresh() {
        if (MeteoService.shared.cities.isEmpty()) {
            loading_view.visibility = View.VISIBLE
            city_list_view.visibility = View.GONE
        } else {
            loading_view.visibility = View.GONE
            city_list_view.visibility = View.VISIBLE

            if (!MeteoService.shared.cities[0].isCurr) {
                for (city in MeteoService.shared.cities) {
                    if (city.isCurr) {
                        removeCurrentPosFromArray()
                        MeteoService.shared.cities.add(0, city)

                    }
                }
            }
            city_list_view.adapter = CityCardAdapter(this, MeteoService.shared.cities)
            city_list_view.setOnItemLongClickListener { parent, view, position, id ->
                if (!MeteoService.shared.cities[position].isCurr) {
                    val countries = listOf("Delete", "Cancel")
                    selector("Where are you from?", countries, { dialogInterface, i ->
                        when (countries[i]) {
                            "Delete" -> {
                                MeteoService.shared.cities.removeAt(i)
                                val sharedPref = this.getSharedPreferences(
                                        MeteoService.shared.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                                with(sharedPref.edit()) {
                                    putStringSet(getString(R.string.shared_pref_key), MeteoService.shared.getCityNames())
                                    apply()
                                }
                                refresh()
                            }
                            else -> dialogInterface.dismiss()
                        }
                    })
                }
                true
            }
        }

    }

    fun removeCurrentPosFromArray() {
        var i = 0
        while (i < MeteoService.shared.cities.count()) {
            if (MeteoService.shared.cities[i].isCurr) {
                MeteoService.shared.cities.removeAt(i)
                return
            }
            i++
        }
    }

    private fun requestAndgetData(queue: RequestQueue, url: String, currentLoc: Boolean) {
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val klaxon = Klaxon()
                    var data = klaxon.parse<WeatherData>(response)
                    data?.isCurr = currentLoc
                    if (data != null) {
                        MeteoService.shared.cities.add(data)
                    }
                    if (data?.isCurr != true) {
                        val sharedPref = this.getSharedPreferences(
                                MeteoService.shared.SHARED_PREF_NAME, Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            putStringSet(getString(R.string.shared_pref_key), MeteoService.shared.getCityNames())
                            apply()
                        }
                    }
                    refresh()
                },
                Response.ErrorListener {
                    runOnUiThread {
                        loading_text.text = it.toString()
                    }
                })

        queue.add(stringRequest)
    }

    private fun getLocationWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getWeather(location.longitude, location.latitude)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PREMISSION_LOCATION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocationWeather()
                }
                return
            }
            else -> { // Ignore all other requests. }
            }
        }
    }

}
