package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.setNewValue
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        private const val LOCATION_PERMISSION_INDEX = 0
        private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
    }

    //Reference Treasure Hunt App
    private val runningQOrLater =
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }

    lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel


        val representativeAdapter = RepresentativeListAdapter()
        binding.rvRepresentatives.adapter = representativeAdapter

        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                representativeAdapter.submitList(it)
            }
        })

        viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.state.setNewValue(it.state)
            }
        })

        binding.buttonSearch.setOnClickListener {
            viewModel.getRepresentatives(viewModel.address.value.toString())
            hideKeyboard()
        }

        binding.buttonLocation.setOnClickListener {
            requestForegroundAndBackgroundLocationPermissions()
        }

        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        LocationServices.getFusedLocationProviderClient(requireContext())
                .lastLocation.addOnSuccessListener { location ->
                    viewModel.getAddressFromLocation(geoCodeLocation(location))
                    viewModel.getRepresentatives(viewModel.address.value.toString())
                }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (
                grantResults.isEmpty() ||
                grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
                (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                        grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                        PackageManager.PERMISSION_DENIED)
        ) {
            // Permission denied.
            Snackbar.make(
                    binding.searchArea,
                    R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok) {
            }.show()
        } else {
            println("onRequestPermissionsResult == true")
            getLocation()
        }
    }

    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            getLocation()
        } else {
            var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            val resultCode = when {
                runningQOrLater -> {
                    permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
                }
                else ->
                    REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            }
            requestPermissions(
                    permissionsArray,
                    resultCode
            )
        }
    }

    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved =
                (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val backgroundLocationApproved =
                if (runningQOrLater) {
                    PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                } else {
                    true
                }
        return foregroundLocationApproved && backgroundLocationApproved
    }

}