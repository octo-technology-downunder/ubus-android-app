package com.octo.nickshulhin.ubus.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.octo.nickshulhin.ubus.R
import com.octo.nickshulhin.ubus.database.Connectivity
import com.octo.nickshulhin.ubus.listeners.OnDataReceivedListener
import com.octo.nickshulhin.ubus.model.DataModel
import java.util.*


/**
 * Created by nickshulhin on 23/3/18.
 */

class MapViewFragment : Fragment() {

    var mGoogleMap: GoogleMap? = null
    var dataModel: DataModel = DataModel()
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.map_view_fragment_layout, container, false)
        val mapView: MapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        prepareMaps(mapView)
        mapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                mGoogleMap = googleMap
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(context.applicationContext)
                val sydney = LatLng(-34.0, 151.0)
                mGoogleMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                setUpSearchLocationFragment()
                setUpMapClickListener(object : OnDataReceivedListener<DataModel> {
                    override fun onDataReceived(data: DataModel) {
                        dataModel.endLat = data.endLat
                        dataModel.endLong = data.endLong
                    }
                })
                setUpPushButton(view)
            }
        })
        return view
    }

    fun setUpMapClickListener(listener: OnDataReceivedListener<DataModel>) {
        mGoogleMap!!.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
            override fun onMapClick(point: LatLng) {
                mGoogleMap!!.clear()
                val marker = mGoogleMap!!.addMarker(MarkerOptions().position(point))
                listener.onDataReceived(DataModel(endLat = marker.position.latitude, endLong = marker.position.longitude))
            }
        });
    }

    fun setUpSearchLocationFragment() {

        val fragment = SupportPlaceAutocompleteFragment()

        fragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val cameraPosition: CameraPosition = CameraPosition.Builder().target(place.latLng).zoom(15f).build()
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            override fun onError(status: Status) {
                Log.i("SEARCH", "An error occurred: " + status)
            }
        })

        val transaction = childFragmentManager
                .beginTransaction()
                .replace(R.id.search_address, fragment)
        transaction.commit()

    }

    fun setUpHookListener(hookId: String) {
        Connectivity.subscribeForHookID(hookId, object : OnDataReceivedListener<String> {
            override fun onDataReceived(data: String) {
                Toast.makeText(context, "Firebase changed: " + data, Toast.LENGTH_SHORT).show();
            }
        })
    }

    fun setUpPushButton(view: View) {
        val pushButton: Button = view.findViewById(R.id.map_button_push_id)
        pushButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }

                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    Log.i("Location", "My location is " + location!!.longitude + "," + location.latitude)

                        dataModel.startLat = location.latitude
                        dataModel.startLong = location.longitude

                        Connectivity.pushLocation(dataModel, object : OnDataReceivedListener<String> {
                            override fun onDataReceived(data: String) {
                                (view.context as Activity).runOnUiThread(object : Runnable {
                                    override fun run() {
                                        Toast.makeText(view.context, "Received data: " + data, Toast.LENGTH_SHORT).show()
                                        //setUpHookListener(data)
                                    }
                                })
                            }
                        })
                }

            }
        })
    }

    fun prepareMaps(mapView: MapView) {
        mapView.getMapAsync({ mMap ->
            mGoogleMap = mMap
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setUpLocation()
            } else {
                val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        setUpLocation()
    }

    fun setUpLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap!!.setMyLocationEnabled(true)
        mGoogleMap!!.getUiSettings().setMyLocationButtonEnabled(true)
        mGoogleMap!!.getUiSettings().setCompassEnabled(true)
    }
}