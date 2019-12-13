package com.julio.project_security_essencials

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
    LocationListener {

    private lateinit var email: TextView
    private lateinit var latLong: TextView
    private lateinit var apiClient: GoogleApiClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        email = findViewById(R.id.textViewCorreo)
        latLong = findViewById(R.id.textViewLatLong)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        locationRequest = LocationRequest()

        leerSharedPreferences()

        apiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            email.text = currentUser.email
        }
    }

    private fun leerSharedPreferences(){
        val preference: SharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        preference.getString("uid", "")
        email.text = preference.getString("email", "")
    }

    private fun obtenerDatosUsuario(){
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
        }
    }

    private fun updateLocation(){
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this)
    }

    private fun updateUI(loc: Location?) {
        if (loc != null) {
            latLong.text = "Latitud: ${loc.latitude}\n" +
                    "Longitud: ${loc.longitude}\n"
        } else {
            latLong.text = "Latitud: (No capturada)\nLongitud: (No capturada)"
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.
        Log.e(LOG_TAG, "Error grave al conectar con Google Play Services")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PETITION_PERMIT_LOCATION) {
            if (grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) { //Permiso concedido
                val location =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient)
                updateUI(location)
            } else { //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e(LOG_TAG, "Permiso denegado")
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        //Conectado correctamente a Google Play Services
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PETITION_PERMIT_LOCATION
            )
        } else {
            updateLocation()
            val location = LocationServices.FusedLocationApi.getLastLocation(apiClient)
            updateUI(location)
        }
        //startLocationUpdate()
    }

    override fun onConnectionSuspended(p0: Int) {
        //Se ha interrumpido la conexión con Google Play Services
        Log.e(LOG_TAG, "Se ha interrumpido la conexión con Google Play Services")
    }

    override fun onLocationChanged(location: Location?) {
        updateLocation()
        updateUI(location)
    }

    companion object {
        const val PETITION_PERMIT_LOCATION = 101
        const val UPDATE_INTERVAL: Long = 500
        const val FASTEST_INTERVAL: Long = 500
        const val LOG_TAG:String = "Mensaje_Home"
    }

}
