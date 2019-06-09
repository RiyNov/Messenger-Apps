package com.riynov.kotlin.riymessenger.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.riynov.kotlin.riymessenger.R
import com.riynov.kotlin.riymessenger.messages.MessengerActivity
import com.riynov.kotlin.riymessenger.models.Users
import kotlinx.android.synthetic.main.register_activity.*
import java.util.*


class RegisterPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        btn_reg_register.setOnClickListener {

            RegistrationProsess()

        }

        tv_reg_haveAccount.setOnClickListener {
            Log.d("RegisterPage","to Login page we go..")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        btn_reg_photo.setOnClickListener {
            Log.d("RegisterPage","Try to show photo ")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)

        }
    }

    var selectedPhotoURI: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //Proceed and check what the selected img was ...
            Log.d("RegisterPage","Photo was selected")

            selectedPhotoURI = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoURI)

            reg_photoPreview.setImageBitmap(bitmap)
            btn_reg_photo.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            btn_reg_photo.setBackgroundDrawable(bitmapDrawable)
        }

    }

    private fun RegistrationProsess() {
        val email = et_reg_email.text.toString()
        val password = et_reg_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter Email/Password",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterPage","Email is: $email")
        Log.d("RegisterPage","Password is: $password")

        //Firebase Authentication (Create user with email and password)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if success
                Log.d("Register","Successfully create user with UID: ${it.result!!.user.uid}")

                uploadImageToFirebase()
            }
            .addOnFailureListener {
                Log.d("Register","Failed to create user -> ${it.message}")
                Toast.makeText(this, "${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebase() {

        if (selectedPhotoURI == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/photos/$filename")

        ref.putFile(selectedPhotoURI!!)
            .addOnSuccessListener {
                Log.d("RegisterPage","Success upload photo: ${it.metadata!!.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterPage","Photos location is at $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("RegisterPage","Failed to upload photo to storage -> ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageURL: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user =
            Users(uid, et_reg_username.text.toString(), profileImageURL)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterPage","Success put user data to Firebase database")

                val intent = Intent(this, MessengerActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("RegisterPage","Failed to save user to database -> ${it.message}")
            }
    }

}
