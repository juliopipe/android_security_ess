package com.julio.project_security_essencials

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var nombre: TextView
    private lateinit var email: TextView
    private lateinit var latLong: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        nombre = findViewById(R.id.textViewNombre)
        email = findViewById(R.id.textViewCorreo)
        latLong = findViewById(R.id.textViewLatLong)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            // se deben  mostrar los datos en la interfaz
            if(currentUser.displayName != null){
                nombre.text = "No tiene"
            } else {
                nombre.text = currentUser.displayName
            }
            email.text = currentUser.email
        }
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

}
