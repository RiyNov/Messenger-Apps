package com.riynov.kotlin.riymessenger.registerlogin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.riynov.kotlin.riymessenger.R
import kotlinx.android.synthetic.main.login_activity.*


class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        btn_login_login.setOnClickListener {

            LoginProcess()

        }

        tv_login_signUp.setOnClickListener {
            finish()
        }

    }

    private fun LoginProcess() {
        val email = et_login_email.text.toString()
        val password = et_login_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email/Password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Login", "Attempt login with Email: $email and Password $password")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (!it.isSuccessful) return@addOnCompleteListener
//
//                //Else if success
//
//            }

    }

}