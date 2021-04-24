package com.example.weather.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.weather.R
import com.example.weather.data.Hourly
import com.example.weather.databinding.HourlyWeatherItemBinding
import java.util.*
import kotlin.collections.ArrayList

class HourlyWeatherRecyclerViewAdapter : RecyclerView.Adapter<HourlyWeatherRecyclerViewAdapter.HourlyWeatherViewHolder>() {

    class HourlyWeatherViewHolder(val hourlyWeatherViewBinding: HourlyWeatherItemBinding) : RecyclerView.ViewHolder(hourlyWeatherViewBinding.root)

    var binding: HourlyWeatherItemBinding? = null
    var hourlyWeatherList: List<Hourly> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        binding = HourlyWeatherItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HourlyWeatherViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        with(holder.hourlyWeatherViewBinding) {
            hourTextView.text = getHour(position)
            setTemperatureIcon("https://openweathermap.org/img/wn/${hourlyWeatherList[position].weather[0].icon}.png", tempImageView)
            tempTextView.text = "${hourlyWeatherList[position].temp}\u00B0"
            feelsLikeTextView.text = root.context.getString(R.string.feelsLike, hourlyWeatherList[position].feels_like)
        }
    }

    override fun getItemCount() = hourlyWeatherList.size

    private fun getHour(position: Int): String  {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return "${(hour + position * 3) % 24}:00"
    }

    private fun setTemperatureIcon(imageUrl: String, imageView: ImageView) = Glide
            .with(imageView.context)
            .load(imageUrl)
            .error(R.drawable.n_a)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
}