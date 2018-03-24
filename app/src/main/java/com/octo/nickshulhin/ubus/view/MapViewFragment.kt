package com.octo.nickshulhin.ubus.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.octo.nickshulhin.ubus.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.common.api.GoogleApiClient
import android.support.v4.content.ContextCompat
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.Button
import com.octo.nickshulhin.ubus.database.Connectivity
import com.octo.nickshulhin.ubus.listeners.OnDataReceivedListener
import kotlinx.android.synthetic.main.map_view_fragment_layout.*
import java.util.*


/**
 * Created by nickshulhin on 23/3/18.
 */

class MapViewFragment : Fragment() {

    var mGoogleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val uid = UUID.randomUUID().toString()
        val view = inflater.inflate(R.layout.map_view_fragment_layout, container, false)
        val mapView: MapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        prepareMaps(mapView)
        mapView.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(googleMap: GoogleMap) {
                mGoogleMap = googleMap
                val sydney = LatLng(-34.0, 151.0)
                mGoogleMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
            }
        })
        setUpPushButton(view, uid)
        return view
    }

    fun setUpHookListener(hookId: String){
        Connectivity.subscribeForHookID(hookId, object:OnDataReceivedListener{
            override fun onDataReceived(data: String) {

            }
        })
    }

    fun setUpPushButton(view: View, hookId: String) {
        val pushButton: Button = view.findViewById(R.id.map_button_push_id)
        pushButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Connectivity.pushHookID(hookId)
                setUpHookListener(hookId)
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