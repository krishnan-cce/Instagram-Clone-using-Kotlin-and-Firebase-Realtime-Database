package com.example.mj

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_signin.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        signin_link_btn.setOnClickListener{
            startActivity(Intent(this,SignInActivity::class.java))
        }

        signup_btn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val username: String = username_signup.text.toString()
        val fullname: String = fullname_signup.text.toString()
        val email: String = email_signup.text.toString()
        val password: String = password_signup.text.toString()

        when{
            TextUtils.isEmpty(username) -> Toast.makeText(this, "please write username.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(fullname) -> Toast.makeText(this, "please write fullname.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "please write email.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "please write password.", Toast.LENGTH_LONG).show()

            else ->{
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful)
                        {
                            saveUserInfo(username,fullname,email,progressDialog)
                        }
                        else
                        {
                            val message = task.exception.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }

    }

    private fun saveUserInfo(username: String, fullname: String, email: String,progressDialog:ProgressDialog)
    {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userHashMap = HashMap<String, Any>()
        userHashMap["uid"] = currentUserID
        userHashMap["username"] = username.toLowerCase()
        userHashMap["fullname"] = fullname.toLowerCase()
        userHashMap["email"] = email
        userHashMap["status"] = "offline"
        userHashMap["bio"] = "Hey i am using Instagram"
        userHashMap["image"] = "https://firebasestorage.googleapis.com/v0/b/bxase-42928.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=6e7def62-9de5-410f-8d9f-4087db9e2406"



        userRef.child(currentUserID).setValue(userHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Account has been Created Succesfully", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val message = task.exception.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}