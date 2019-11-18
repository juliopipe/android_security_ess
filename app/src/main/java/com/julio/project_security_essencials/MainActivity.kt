package com.julio.project_security_essencials

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var correo: TextView
    private lateinit var clave: TextView
    private lateinit var entrar: Button
    private lateinit var registrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        correo = findViewById(R.id.editTextCorreoL)
        clave = findViewById(R.id.editTextClaveL)
        entrar = findViewById(R.id.buttonEntrarL)
        registrar = findViewById(R.id.buttonRegistrarL)


        entrar.setOnClickListener {
            val home = Intent(this, HomeActivity::class.java)
            startActivity(home)
        }

        registrar.setOnClickListener {
            val createAccount = Intent(this, CreateAccountActivity::class.java)
            startActivity(createAccount)
        }
    }
}
