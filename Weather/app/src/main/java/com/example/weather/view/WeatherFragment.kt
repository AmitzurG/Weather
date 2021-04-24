package com.example.weather.view

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.weather.R
import com.example.weather.data.WeatherObject
import com.example.weather.databinding.FragmentWeatherBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class WeatherFragment : Fragment() {

    private var binding: FragmentWeatherBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHourlyWeatherRecyclerView()
        setWeather((activity as MainActivity).weatherViewModel.units)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.menu_weather, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.actionList -> {
                val action = WeatherFragmentDirections.actionWeatherFragmentToMultiCityFragment()
                findNavController().navigate(action)
                true
            }
            R.id.actionRefresh -> {
                setWeather((activity as MainActivity).weatherViewModel.units)
                true
            }
            R.id.actionToggleUnits -> {
                if ((activity as MainActivity).weatherViewModel.units == "metric") {
                    setWeather("imperial")
                } else {
                    setWeather("metric")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setTitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.title =
                if ((activity as MainActivity).weatherViewModel.units == "metric") getString(R.string.celsius)
                else getString(R.string.fahrenheit)
    }

    private fun setHourlyWeatherRecyclerView() = binding?.hourlyRecyclerView?.apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.HORIZONTAL)
        addItemDecoration(dividerItemDecoration)
        adapter = HourlyWeatherRecyclerViewAdapter()
    }

    private fun setWeather(units: String = "metric") {
        val viewModel = (activity as MainActivity).weatherViewModel
        viewModel.units = units
        (activity as MainActivity).getCurrentLocation {
            if (it != null) {
                showProgressBar()
                setTitle()
                viewModel.getWeatherByGeoCoordinates(it.latitude.toString(), it.longitude.toString(), units).observe(viewLifecycleOwner, { weatherObject ->
                    setMainCard(weatherObject)
                    setSunriseSunsetCard(weatherObject)
                    setDetailsCard(weatherObject)
                })

                viewModel.getHourlyWeather(it.latitude.toString(), it.longitude.toString(), units).observe(viewLifecycleOwner, { hourlyWeatherList ->
                    (binding?.hourlyRecyclerView?.adapter as? HourlyWeatherRecyclerViewAdapter)?.hourlyWeatherList = hourlyWeatherList
                    showProgressBar(false)
                })
            }
        }
    }

    private fun setMainCard(weatherObject: WeatherObject) {
        setWeatherIcon("https://openweathermap.org/img/wn/${weatherObject.weather[0].icon}.png", binding?.tempImageView)
        binding?.apply {
            dateTimeTextView.text = DateFormat.getDateTimeInstance().format(Date())
            tempTextView.text = "${weatherObject.main.temp}\u00B0"
            minMaxTextView.text = getString(R.string.minMaxTemperature, weatherObject.main.temp_min, weatherObject.main.temp_max)
            nameTextView.text = weatherObject.name
            descriptionTextView.text = weatherObject.weather[0].description
        }
    }

    private fun setSunriseSunsetCard(weatherObject: WeatherObject) {
        val dateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
        binding?.apply {
            sunriseTextView.text = dateFormat.format(Date(weatherObject.sys.sunrise * 1000 /*in milliseconds*/))
            sunsetTextView.text = dateFormat.format(Date(weatherObject.sys.sunset * 1000))
        }
    }

    private fun setDetailsCard(weatherObject: WeatherObject) {
        binding?.apply {
            windSpeedTextView.text = getString(R.string.windSpeed, weatherObject.wind.speed)
            cloudinessTextView.text = getString(R.string.cloudiness, weatherObject.weather[0].description)
            humidityTextView.text = getString(R.string.humidity, weatherObject.main.humidity)
            pressureTextView.text = getString(R.string.pressure, weatherObject.main.pressure)
            visibilityTextView.text = getString(R.string.visibility, weatherObject.visibility)
        }
    }

    private fun setWeatherIcon(imageUrl: String, imageView: ImageView?) {
        if (imageView != null) {
            Glide.with(requireContext()).load(imageUrl).error(R.drawable.n_a).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
        }
    }

    private fun showProgressBar(show: Boolean = true) {
        binding?.waitingProgressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }
}