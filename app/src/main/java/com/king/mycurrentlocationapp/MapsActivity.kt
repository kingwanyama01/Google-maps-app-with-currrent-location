package com.king.mycurrentlocationapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    var latitude = 0.0
    var longitude = 0.0
    private var fusedLocationClient: FusedLocationProviderClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermission
        fetchGPS()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fetchGPS()
        // Add a marker in Sydney and move the camera
    }

    fun fetchGPS() {
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient!!.lastLocation
                .addOnSuccessListener(
                        this
                ) { location ->
                    Toast.makeText(this@MapsActivity, "Fetched", Toast.LENGTH_SHORT).show()
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) { // Logic to handle location object
                        val lat = location.latitude
                        val lon = location.longitude
                        Toast.makeText(
                                this@MapsActivity,
                                "$lat:$lon",
                                Toast.LENGTH_LONG
                        ).show()
                        latitude = lat
                        longitude = lon
                        if (mMap != null) {
                            val sydney = LatLng(lat, lon)
                            mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in My Location"))
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
                        }
                    } else {
                        Toast.makeText(this@MapsActivity, "Null", Toast.LENGTH_SHORT).show()
                        fetchGPS()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this@MapsActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
    }

    var mLocationPermissionGranted = false
    private val locationPermission: Unit
        private get() {
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionGranted = true
            } else {
                ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                    //getDeviceLocation();
                    fetchGPS()
                }
            }
        }
        //  updateLocationUI();
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2000
    }
}