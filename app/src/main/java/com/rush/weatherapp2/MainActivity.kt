package com.rush.weatherapp2

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.rush.weatherapp2.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//694a6b3af45e036adc76f512f2f99256
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("london")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"694a6b3af45e036adc76f512f2f99256","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null)
                {
                    val temperature = responseBody.main.temp.toString()
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val sea = responseBody.main.pressure

                    binding.temperature.text = "$temperature °C"
                    binding.weather.text = "$condition"
                    binding.maxtemp.text = "MAX TEMP: $maxTemp °C"
                    binding.mintemp.text = "MIN TEMP: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.condition.text = "$condition"
                    binding.sunrise.text= "${time(sunRise)}"
                    binding.sunset.text= "${time(sunSet)}"
                    binding.sea.text= "$sea hPa"
                    binding.cityName.text = "$cityName"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()

                    changeImageAccordingToWeatherCondition(condition)
                    //Log.d("TAG", "onResponse: $temperature")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

        }

    private fun changeImageAccordingToWeatherCondition(conditions: String) {
        when(conditions){
            "Haze","Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.imageView2.setImageResource(R.drawable.cloud_black)
            }
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.imageView2.setImageResource(R.drawable.sunny)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.imageView2.setImageResource(R.drawable.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard","Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.imageView2.setImageResource(R.drawable.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.imageView2.setImageResource(R.drawable.sunny)
            }
        }
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format(Date())
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

    fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
}