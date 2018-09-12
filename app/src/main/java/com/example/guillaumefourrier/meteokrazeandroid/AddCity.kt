package com.example.guillaumefourrier.meteokrazeandroid

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.widget.ProgressBar
import android.widget.Toast
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.progressDialog
import java.io.Serializable


class AddCity : AppCompatActivity(), Serializable {

    var cities = ArrayList<Cities>()
    private val apiKey = "4271a4992f162462f555468b8aa580f2"

    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        autocomplete_text_view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                this.dialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Fetching data")
                addCity(autocomplete_text_view.text.toString().trim())
            }
            true
        }
    }

    fun addCity(cityName: String) {
        val queue = Volley.newRequestQueue(this)

        val url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    val klaxon = Klaxon()
                    var data = klaxon.parse<WeatherData>(response)
                    data?.isCurr = false
                    if (data != null) {
                        MeteoService.shared.cities.add(data)
                    }
                    val returnIntent = Intent()
                    returnIntent.putExtra("city", data)
                    setResult(Activity.RESULT_OK, returnIntent)
                    this.dialog?.dismiss()
                    finish()

                },
                Response.ErrorListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    this.dialog?.dismiss()
                    finish()

                })

        queue.add(stringRequest)
    }

    override fun onNavigateUp(): Boolean {
        if (!autocomplete_text_view.text.isEmpty()) {
            this.dialog = indeterminateProgressDialog(message = "Please wait a bit…", title = "Fetching data")
            addCity(autocomplete_text_view.text.toString().trim())
        } else {
            super.onNavigateUp()
            finish()
        }
        super.onNavigateUp()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

}