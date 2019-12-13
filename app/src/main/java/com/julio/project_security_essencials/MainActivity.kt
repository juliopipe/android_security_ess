package com.julio.project_security_essencials

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    private lateinit var correo: TextView
    private lateinit var clave: TextView
    private lateinit var entrar: Button
    private lateinit var registrar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var createAccount:FirebaseAuth

    private val keySecret: String = "julio123456789ac"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        correo = findViewById(R.id.editTextCorreoL)
        clave = findViewById(R.id.editTextClaveL)
        entrar = findViewById(R.id.buttonEntrarL)
        registrar = findViewById(R.id.buttonRegistrarL)
        auth = FirebaseAuth.getInstance()

        entrar.setOnClickListener {
            logear(correo.text.toString(), String.encrypt(clave.text.toString()))
        }

        registrar.setOnClickListener {
            val createAccount = Intent(this, CreateAccountActivity::class.java)
            startActivity(createAccount)
        }
    }

    private fun almacenarSharedPreferences(user: User){
        val preference: SharedPreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val  editor: SharedPreferences.Editor = preference.edit()
        editor.putString("uid", user.UID)
        editor.putString("email", user.email)
        editor.commit()
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
                    val userFirebase = auth.currentUser
                    val user = User(userFirebase!!.uid, userFirebase!!.email)
                    almacenarSharedPreferences(user)
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

    private fun String.Companion.encrypt(password: String): String {
        val secretKeySpec = SecretKeySpec(keySecret.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for (i in charArray.indices) {
            iv[i] = charArray[i].toByte()
        }
        val ivParameterSPec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSPec)
        val encryptedValue = cipher.doFinal()
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
    }

    private fun String.Companion.decrypt(password: String): String{
        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for(element in charArray){
            iv[0] = element.toByte()
        }
        val ivParameterSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decryptedByteValue = cipher.doFinal()
        return String(decryptedByteValue)
    }
    
}
