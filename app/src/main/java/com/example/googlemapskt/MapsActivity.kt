package com.example.googlemapskt

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.googlemapskt.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(listener)



        // Add a marker in Sydney and move the camera
        /*
        val sydney = LatLng(-34.0, 151.0)
        val adana= LatLng(36.996799,35.321002)
        mMap.addMarker(MarkerOptions().position(adana).title("Marker in Atatürk Parkı"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(adana,15f))*/
        //cast işlemleri
        locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener= object :  LocationListener {
            override fun onLocationChanged(location: Location) {
                //lockasyon değişiminde yapılıcak işlemler
                //println(location.latitude)
                //println(location.longitude)
                mMap.clear()
                //bu ise guncellenen konumu
                val guncelLocation=LatLng(location.latitude,location.longitude)
                mMap.addMarker(MarkerOptions().position(guncelLocation).title("Guncel Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelLocation,15f))
                val geocoder=Geocoder(this@MapsActivity, Locale.getDefault())

                try {
                    //string oalrak adresi alma
                    val addressList=geocoder.getFromLocation(location.latitude,location.longitude,1)
                    if(addressList.size>0){
                        println(addressList[0].toString())
                    }

                }catch (e: Exception){
                    e.printStackTrace()
                }

            }
        }
        if(ContextCompat
                .checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //izin verilmedi
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        else{
            //izin verildi
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            val lastKnowLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastKnowLocation!=null){
                //son konumu almak interneti yoksa diye
                val lastKnowLatLng=LatLng(lastKnowLocation.latitude,lastKnowLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnowLatLng).title("Son Bilinen Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnowLatLng,15f))
            }
        }

    }
    //izin işlemleri
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if (grantResults.size>0){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                    //izin verildi
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val listener= object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            mMap.clear()
            val geocoder=Geocoder(this@MapsActivity,Locale.getDefault())

            if(p0 != null){
                var address=""
                try {
                    val addressList=geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if(addressList.size>0){
                        if(addressList[0].thoroughfare!=null){
                            address+=addressList[0].thoroughfare
                            if(addressList[0].subThoroughfare!=null){
                                address+=" No :"+addressList[0].subThoroughfare
                            }
                        }

                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(p0).title(address))
            }



        }

    }
}