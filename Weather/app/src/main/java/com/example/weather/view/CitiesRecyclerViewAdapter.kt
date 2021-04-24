package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.weather.R
import com.example.weather.data.WeatherEntity
import com.example.weather.databinding.WeatherCityItemBinding

class CitiesRecyclerViewAdapter : RecyclerView.Adapter<CitiesRecyclerViewAdapter.CityWeatherViewHolder>() {

    class CityWeatherViewHolder(val cityWeatherViewBinding: WeatherCityItemBinding) : RecyclerView.ViewHolder(cityWeatherViewBinding.root)

    var binding: WeatherCityItemBinding? = null
    var citiesWeather = mutableListOf<WeatherEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onCityWeatherClick: (WeatherEntity) -> Unit = {}
    var onCityWeatherLongClick: (WeatherEntity) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityWeatherViewHolder {
        binding = WeatherCityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CityWeatherViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: CityWeatherViewHolder, position: Int) {
        with(holder.cityWeatherViewBinding) {
            setOnItemClick(weatherItemView, citiesWeather[position])
            setOnItemLongClick(weatherItemView, citiesWeather[position])
            setTempIcon("https://openweathermap.org/img/wn/${citiesWeather[position].icon}.png", tempImageView)
            cityNameTextView.text = citiesWeather[position].name
            tempTextView.text = "${citiesWeather[position].temp}\u00B0"
            descriptionTextView.text = citiesWeather[position].description
            minMaxTempTextView.text = root.context.getString(R.string.minMaxTemperature, citiesWeather[position].temp_min, citiesWeather[position].temp_max)
        }
    }

    override fun getItemCount() = citiesWeather.size

    private fun setOnItemClick(weatherItemView: View, weather: WeatherEntity) = weatherItemView.setOnClickListener {
        onCityWeatherClick(weather)
    }

    private fun setOnItemLongClick(weatherItemView: View, weather: WeatherEntity) = weatherItemView.setOnLongClickListener {
        onCityWeatherLongClick(weather)
        true
    }

    private fun setTempIcon(imageUrl: String, imageView: ImageView) = Glide
        .with(imageView.context)
        .load(imageUrl)
        .error(R.drawable.n_a)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(imageView)
}