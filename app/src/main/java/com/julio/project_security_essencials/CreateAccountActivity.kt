package com.julio.project_security_essencials

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var correo: TextView
    private lateinit var clave: TextView
    private lateinit var confirmarClave: TextView
    private lateinit var login: Button
    private lateinit var registrar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        correo = findViewById(R.id.editTextCorreoCC)
        clave = findViewById(R.id.editTextClaveCC)
        confirmarClave = findViewById(R.id.editTextConfirmarClaveCC)
        login = findViewById(R.id.buttonEntrarCC)
        registrar = findViewById(R.id.buttonRegistrarCC)

        login.setOnClickListener {
            val login = Intent(this, MainActivity::class.java)
            startActivity(login)
        }

        registrar.setOnClickListener {

        }
    }
}
