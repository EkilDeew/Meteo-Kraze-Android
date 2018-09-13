package com.example.guillaumefourrier.meteokrazeandroid

import android.content.Context
import java.text.SimpleDateFormat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class CityCardAdapter(private val context: Context,
                      private val data: ArrayList<WeatherData>) : BaseAdapter() {

        private val mInflator: LayoutInflater

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return data.size
        }

        override fun getItem(position: Int): Any {
            return data[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.city_weather, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }

            val city = data[position]

            if (city.isCurr) {
                vh.isCurrentPos.visibility = View.VISIBLE
            } else {
                vh.isCurrentPos.visibility = View.GONE
            }

            vh.cityName.text = city.name
            vh.description.text = city.weather[0].description
            vh.currentTemp.text = city.main.temp.minus(273.15).toInt().toString() + "°C"
            vh.maxTemp.text = "Max : " + city.main.temp_max.minus(273.15).toInt().toString() + "°C"
            vh.minTemp.text = "Min : " + city.main.temp_min.minus(273.15).toInt().toString() + "°C"
            vh.humidity.text = "Humidity : " + city.main.humidity + "%"
            vh.wind.text = "Wind : " + city.wind.speed + " m/s"

            val sf = SimpleDateFormat("HH:mm")
            val sunsetTT = Date(city.sys.sunset * 1000L) // ???? ?? ?? ??  @TODO
            val sunriseTT = Date(city.sys.sunrise * 1000L)
            vh.sunrise.text = "Sunrise : " + sf.format(sunriseTT)
            vh.sunset.text = "Sunset : " + sf.format(sunsetTT)

            return view
    }

    private class ListRowHolder(row: View?) {

        val isCurrentPos: TextView
        val cityName: TextView
        val description: TextView
        val currentTemp: TextView
        val minTemp: TextView
        val maxTemp: TextView
        val humidity: TextView
        val wind: TextView
        val sunrise: TextView
        val sunset: TextView

        init {
            this.isCurrentPos = row?.findViewById(R.id.current_pos_text) as TextView
            this.cityName = row.findViewById(R.id.city_name) as TextView
            this.description = row.findViewById(R.id.weather_description) as TextView
            this.currentTemp = row.findViewById(R.id.weather_curr_temp) as TextView
            this.minTemp = row.findViewById(R.id.weather_min_temp) as TextView
            this.maxTemp = row.findViewById(R.id.weather_max_temp) as TextView
            this.humidity = row.findViewById(R.id.weather_humidity) as TextView
            this.wind = row.findViewById(R.id.weather_wind) as TextView
            this.sunrise = row.findViewById(R.id.weather_sunrise) as TextView
            this.sunset = row.findViewById(R.id.weather_sunset) as TextView
        }
    }

}
