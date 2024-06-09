package com.example.real_timeweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import com.example.real_timeweatherapp.databinding.ActivityMainBinding
import com.google.android.material.color.utilities.ViewingConditions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//2782a180b70c48b7ed12d3542fdaff2f
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        fetchWeatherData("bharuch")
        searchCity()
    }

    private fun searchCity() {

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null)  {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "df41af2f12fac9a3daf76e50ba4ad522", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humid = responseBody.main.humidity
                    val max = responseBody.main.temp_max
                    val min = responseBody.main.temp_min
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val wind = responseBody.wind.speed
                    val sunset = responseBody.sys.sunset.toLong()
                    val sea = responseBody.main.sea_level
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"

                    binding.temp.text = "$temperature °C"
                    binding.humid.text = "$humid %"
                    binding.max.text = "Max Temp: $max °C"
                    binding.weather.text = "$condition"
                    binding.min.text = "Min Temp: $min °C"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.sea.text = "$sea hPa"
                    binding.windspeed.text = "$wind m/s"
                    binding.conditions.text = "$condition"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityname.text = "$cityName"

                    chanageBackgrounds(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun chanageBackgrounds(conditions: String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.backgroundsunny)
                binding.lottieAnimationView.setAnimation(R.raw.sunny)
            }
            "Partly Clouds", "Overcast", "Clouds", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.backgroundcloud)
                binding.lottieAnimationView.setAnimation(R.raw.cloudy)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain" -> {
                binding.root.setBackgroundResource(R.drawable.backgroundrain)
                binding.lottieAnimationView.setAnimation(R.raw.rainy)
            }

            "Light Snow", "Moderate Snow", "Blizzard", "Heavy Snow" -> {
                binding.root.setBackgroundResource(R.drawable.backgroundsnow)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}

private fun <T> Callback<T>.enqueue(callback: Callback<T>) {

}
