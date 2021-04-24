package com.example.weather.view

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.data.WeatherEntity
import com.example.weather.databinding.FragmentMultiCityBinding
import java.util.*

class MultiCityFragment : Fragment() {
    companion object {
        private const val CITY_QUERY_KEY = "cityQueryKey"
    }

    private var binding: FragmentMultiCityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMultiCityBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        queryCity = savedInstanceState?.getString(CITY_QUERY_KEY, "") ?: ""
        setCitiesRecyclerView()
        setCitiesWeather((activity as MainActivity).weatherViewModel.units)
        setCityWeatherItemClickListener()
    }

    override fun onStart() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.currentCities)
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_multi_city, menu)
        val searchItem = menu.findItem(R.id.weatherSearchView)
        val searchView = searchItem?.actionView as? SearchView
        if (searchView != null) {
            setSearchView(searchView)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.actionRefresh -> {
            setCitiesWeather((activity as MainActivity).weatherViewModel.units)
            true
        }
        R.id.actionToggleUnits -> {
            if ( (activity as MainActivity).weatherViewModel.units == "metric") {
                setCitiesWeather("imperial")
            } else {
                setCitiesWeather("metric")
            }
            true
        }
        R.id.actionRestore -> {
            showProgressBar()
            (activity as MainActivity).weatherViewModel.restoreCitiesWeather().observe(viewLifecycleOwner, { newCitiesWeather ->
                (binding?.cityListRecyclerView?.adapter as? CitiesRecyclerViewAdapter)?.citiesWeather = newCitiesWeather
                showProgressBar(false)
            })
            true
        }
        android.R.id.home -> findNavController().navigateUp()
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CITY_QUERY_KEY, queryCity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setCitiesRecyclerView() = binding?.cityListRecyclerView?.apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        adapter = CitiesRecyclerViewAdapter()
    }

    private fun setCitiesWeather(units: String = "metric") {
        (activity as MainActivity).weatherViewModel.units = units
        showProgressBar()
        (activity as MainActivity).weatherViewModel.getCitiesWeather(units).observe(viewLifecycleOwner, {
            if (queryCity.isNotBlank() && searchView != null) {
                searchView?.setQuery(queryCity, true)
            } else {
                (binding?.cityListRecyclerView?.adapter as? CitiesRecyclerViewAdapter)?.citiesWeather = it
            }
            showProgressBar(false)
        })
    }

    private fun setCityWeatherItemClickListener() {
        // on click
        (binding?.cityListRecyclerView?.adapter as? CitiesRecyclerViewAdapter)?.onCityWeatherClick = {
            val action = MultiCityFragmentDirections.actionMultiCityFragmentToCityFragment(it.icon, it.name, it.temp.toFloat(), it.temp_min.toFloat(), it.temp_max.toFloat(), it.description)
            findNavController().navigate(action)
        }

        // on long click
        (binding?.cityListRecyclerView?.adapter as? CitiesRecyclerViewAdapter)?.onCityWeatherLongClick = {
            val dialog = AlertDialogFragment().apply {
                arguments = Bundle()
                arguments?.putString(AlertDialogFragment.TITLE_KEY, this@MultiCityFragment.context?.getString(R.string.deleteCityTitle))
                arguments?.putString(AlertDialogFragment.MESSAGE_KEY, this@MultiCityFragment.context?.getString(R.string.deleteCityMessage) ?: "")
                arguments?.putString(AlertDialogFragment.BUTTON_KEY, this@MultiCityFragment.context?.getString(R.string.delete) ?: "")
            }
            dialog.positiveButtonCallback = {
                (activity as MainActivity).weatherViewModel.deleteCityWeather(it).observe(viewLifecycleOwner, { newCitiesWeather ->
                    (binding?.cityListRecyclerView?.adapter as? CitiesRecyclerViewAdapter)?.citiesWeather = newCitiesWeather
                })
            }
            dialog.show(childFragmentManager, null)
        }
    }

    private var searchView: SearchView? = null
    private fun setSearchView(searchView: SearchView) {
        this.searchView = searchView
        searchView.queryHint = getString(R.string.search)
        searchView.maxWidth = Integer.MAX_VALUE
        setSearchViewListener(searchView)
    }

    private var queryCity = ""
    private fun setSearchViewListener(searchView: SearchView) = searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            searchView.setQuery("", false)
            searchView.isIconified = true
            searchView.setQuery(query, false)
            return true
        }

        override fun onQueryTextChange(query: String) : Boolean {
            val cities = (activity as MainActivity).weatherViewModel.citiesWeather
            val filterCities = cities.filter { it.name.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) }
            (binding?.cityListRecyclerView?.adapter as CitiesRecyclerViewAdapter).citiesWeather = filterCities as MutableList<WeatherEntity>
            queryCity = query
            return true
        }
    })

    private fun showProgressBar(show: Boolean = true) {
        binding?.waitingProgressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }

    // used to delete city
    class AlertDialogFragment : DialogFragment() {
        companion object {
            const val TITLE_KEY = "titleKey"
            const val MESSAGE_KEY = "messageKey"
            const val BUTTON_KEY = "buttonKey"
        }

        var positiveButtonCallback: () -> Unit =  {}

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val title = arguments?.getString(TITLE_KEY) ?: getString(R.string.alert)
            val message = arguments?.getString(MESSAGE_KEY) ?: ""
            val buttonText = arguments?.getString(BUTTON_KEY) ?: getString(R.string.ok)
            val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle(title).setMessage(message)
                    .setPositiveButton(buttonText) { _, _ -> positiveButtonCallback() }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ -> dismiss() }

            return alertDialog.create()
        }
    }
}