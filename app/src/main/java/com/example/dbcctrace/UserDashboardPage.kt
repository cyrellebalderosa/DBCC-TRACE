package com.example.dbcctrace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.dbcctrace.databinding.ActivityAdminDashboardPageBinding
import com.example.dbcctrace.databinding.ActivityUserDashboardPageBinding
import com.google.firebase.auth.FirebaseAuth

class UserDashboardPage : AppCompatActivity() {


    //viewBinding
    private lateinit var binding: ActivityUserDashboardPageBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDashboardPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure ActionBar
        actionBar = supportActionBar!!
        actionBar.title = "User Dashboard"

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