package com.example.weather.view

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.weather.R
import com.example.weather.databinding.FragmentCityBinding


class CityFragment : Fragment() {

    private var binding: FragmentCityBinding? = null
    private val args: CityFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCityBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCityWeather()
    }

    override fun onStart() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = args.cityName
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(R.menu.menu_city, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.actionRefresh -> {
            refreshCityWeather()
            true
        }
        android.R.id.home -> findNavController().navigateUp()
        else -> super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setCityWeather() {
        setWeatherIcon("https://openweathermap.org/img/wn/${args.icon}.png", binding?.tempImageView)
        binding?.tempTextView?.text = "${args.temp}\u00B0"
        binding?.minMaxTempTextView?.text = getString(R.string.minMaxTemperature, args.tempMin, args.tempMax)
        binding?.descriptionTextView?.text = args.description
    }

    private fun refreshCityWeather() {
        val viewModel = (activity as MainActivity).weatherViewModel
        showProgressBar()
        viewModel.getWeather(args.cityName, viewModel.units).observe(viewLifecycleOwner, {
            setWeatherIcon("https://openweathermap.org/img/wn/${it.weather[0].icon}.png", binding?.tempImageView)
            binding?.tempTextView?.text = "${it.main.temp}\u00B0"
            binding?.minMaxTempTextView?.text = getString(R.string.minMaxTemperature, it.main.temp_min, it.main.temp_max)
            binding?.descriptionTextView?.text = it.weather[0].description
            showProgressBar(false)
        })
    }

    private fun setWeatherIcon(imageUrl: String, imageView: ImageView?) {
        if (imageView != null) {
            Glide.with(imageView.context).load(imageUrl).error(R.drawable.n_a).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)
        }
    }

    private fun showProgressBar(show: Boolean = true) {
        binding?.waitingProgressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }
}