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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var correo: TextView
    private lateinit var clave: TextView
    private lateinit var confirmarClave: TextView
    private lateinit var login: Button
    private lateinit var registrar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var dbHelper: DBHelper

    private val keySecret: String = "julio123456789ac"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        correo = findViewById(R.id.editTextCorreoCC)
        clave = findViewById(R.id.editTextClaveCC)
        confirmarClave = findViewById(R.id.editTextConfirmarClaveCC)
        login = findViewById(R.id.buttonEntrarCC)
        registrar = findViewById(R.id.buttonRegistrarCC)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference
        dbHelper = DBHelper(this)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()


        login.setOnClickListener {
            val login = Intent(this, MainActivity::class.java)
            startActivity(login)
        }

        registrar.setOnClickListener {
            val correo = correo.text.toString()
            val  contrasenia = clave.text.toString()
            val  confirmarContrasenia = clave.text.toString()

            when {
                contrasenia != confirmarContrasenia -> {
                    Toast.makeText(baseContext, "Error las claves son incorrectas.",
                        Toast.LENGTH_SHORT).show()
                }
                contrasenia.length < 6 -> {
                    Toast.makeText(baseContext, "La clave debe tener min 6 caracteres.",
                        Toast.LENGTH_SHORT).show()
                }
                else -> {
                    crearCuenta(correo, String.encrypt(contrasenia))
                }
            }
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

    private fun crearCuenta(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val getUser = auth.currentUser
                    val user = User(getUser!!.uid, getUser.email)
                    almacenarSharedPreferences(user)
                    dbHelper.insertRow(user.email!!, user.UID!!)
                    myRef.child("user").child(user.UID!!).setValue(user)
                    cambiarGUIHome()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
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
