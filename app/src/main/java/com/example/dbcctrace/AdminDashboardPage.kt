package com.example.dbcctrace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.AboutUsActivity.AboutUs
import com.example.dbcctrace.databinding.ActivityAdminDashboardPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminDashboardPage : AppCompatActivity() {



    //viewBinding
    private lateinit var binding: ActivityAdminDashboardPageBinding

    //ActionBar
    private lateinit var actionBar: ActionBar
    //private lateinit var logout: TextView

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
        checkUser()


        //logout = findViewById(R.id.adminlogout)


        binding.adminlogout.setOnClickListener {
            clearToken(FirebaseAuth.getInstance().currentUser!!.uid)
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInPage::class.java))

        }

        binding.adminrecords.setOnClickListener {
            startActivity(Intent(this, RecordsMainPage::class.java))
        }

        binding.adminqrcode.setOnClickListener {

            //checkForPermission()
            startActivity(Intent(this, QRCodeScanner::class.java))
        }

        binding.adminnotify.setOnClickListener {

            startActivity(Intent(this, AdminNotifyButtonPage::class.java))
        }

        binding.adminaboutus.setOnClickListener {

            startActivity(Intent(this, AboutUs::class.java))
        }

        binding.admingenerate.setOnClickListener {

            startActivity(Intent(this, AdminGenerateQR::class.java))
        }



    }


    private fun clearToken(userId: String){
        FirebaseDatabase
            .getInstance()
            .getReference("tokens")
            .child(userId)
            .removeValue()
    }

    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){
            //user not null, user is logged in, get user info
            val email = firebaseUser.email
            //set to text view
            binding.textUsername.text = email
        }
        else{
            //user is null, user is not logged in, go to login activity
            startActivity(Intent(this, LogInPage::class.java))
            finish()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }
   

}