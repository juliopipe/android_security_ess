package com.julio.project_security_essencials

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class MainActivity : AppCompatActivity() {

    private lateinit var correo: TextView
    private lateinit var clave: TextView
    private lateinit var entrar: Button
    private lateinit var registrar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var createAccount:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        correo = findViewById(R.id.editTextCorreoL)
        clave = findViewById(R.id.editTextClaveL)
        entrar = findViewById(R.id.buttonEntrarL)
        registrar = findViewById(R.id.buttonRegistrarL)
        auth = FirebaseAuth.getInstance()


        entrar.setOnClickListener {
            logear(correo.text.toString(), clave.text.toString())
        }

        registrar.setOnClickListener {
            val createAccount = Intent(this, CreateAccountActivity::class.java)
            startActivity(createAccount)
        }
    }

    private fun  cambiarGUIHome(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun logear(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    cambiarGUIHome()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    companion object{
        const val TAG = "Mensaje log"
    }
    
}
