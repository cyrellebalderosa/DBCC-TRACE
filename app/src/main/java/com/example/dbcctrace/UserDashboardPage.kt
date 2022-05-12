package com.example.dbcctrace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.AboutUsActivity.AboutUs
import com.example.dbcctrace.databinding.ActivityUserDashboardPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
        checkUser()


        binding.logout.setOnClickListener {

            clearToken(FirebaseAuth.getInstance().currentUser!!.uid)
            firebaseAuth.signOut()
            checkUser()


            startActivity(Intent(this, LogInPage::class.java))
        }

        binding.notes.setOnClickListener {
            startActivity(Intent(this, NotesMainPage::class.java))
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(this, UserProfilePage::class.java))
        }

        binding.aboutus.setOnClickListener {
            startActivity(Intent(this, AboutUs::class.java))
        }
        binding.layoutqrcode.setOnClickListener {
            startActivity(Intent(this, GenerateQRcode::class.java))
        }
        
        binding.layoutnotify.setOnClickListener {
            startActivity(Intent(this, EmailNotifyPage::class.java))
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