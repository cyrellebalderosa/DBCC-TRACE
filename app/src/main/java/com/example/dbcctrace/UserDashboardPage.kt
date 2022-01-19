package com.example.dbcctrace

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
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


        binding.imagemenu.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInPage::class.java))
        }

        binding.notes.setOnClickListener {
            startActivity(Intent(this, NotesMainPage::class.java))
        }

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