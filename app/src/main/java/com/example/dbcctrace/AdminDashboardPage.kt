package com.example.dbcctrace

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.ActionBar
import com.example.dbcctrace.databinding.ActivityAdminDashboardPageBinding
import com.google.firebase.auth.FirebaseAuth
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class AdminDashboardPage : AppCompatActivity() {

    //viewBinding
    private lateinit var binding: ActivityAdminDashboardPageBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "Admin Dashboard"

        firebaseAuth = FirebaseAuth.getInstance()


        binding.logouBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInPage::class.java))

        }



    }




    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }

}