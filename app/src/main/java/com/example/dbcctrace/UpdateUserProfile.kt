package com.example.dbcctrace

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityUpdateUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateUserProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateUserProfileBinding

    private lateinit var firstnameET: EditText
    private lateinit var lastnameET: EditText
    private lateinit var genderET: EditText
    //private lateinit var emailET: EditText
    private lateinit var ageET: EditText
    private lateinit var addET: EditText
    private lateinit var cpnumET: EditText
    private lateinit var savebtn: TextView
    private lateinit var backbtn: ImageView
    private lateinit var logouttv: TextView





    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var user: UsersDB
    private lateinit var id: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstnameET = findViewById(R.id.firstnameET)
        lastnameET = findViewById(R.id.lastnameET)
        genderET = findViewById(R.id.genderET)
        //emailET = findViewById(R.id.emailET)
        ageET = findViewById(R.id.ageET)
        addET = findViewById(R.id.addressET)
        cpnumET = findViewById(R.id.cpnumET)
        savebtn = findViewById(R.id.savebtn)
        backbtn = findViewById(R.id.backbtn)
        logouttv = findViewById(R.id.logoutTV)


        firebaseAuth = FirebaseAuth.getInstance()
        id = firebaseAuth.currentUser?.uid.toString()

        backbtn.setOnClickListener {
            onSupportNavigateUp()
        }
        logouttv.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInPage::class.java))
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("users")



        savebtn.setOnClickListener {

            //val email = binding.emailET.text.toString()
            val firstname = binding.firstnameET.text.toString()
            val lastname = binding.lastnameET.text.toString()
            val age = binding.ageET.text.toString()
            val gender = binding.genderET.text.toString()
            val address = binding.addressET.text.toString()
            val cpnum = binding.cpnumET.text.toString()


            updateData(firstname,lastname,age,gender,address,cpnum)
        }


        }

    private fun updateData(firstname: String, lastname: String, age: String, gender: String, address: String, cpnum: String) {

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val user = mapOf<String,String>(
                "firstname" to firstname,
                "lastname" to lastname,
                "age" to age,
                "gender" to gender,
                "address" to address,
                "phoneNum" to cpnum
        )

        databaseReference.child(id).updateChildren(user).addOnSuccessListener {

            binding.firstnameET.text.clear()
            binding.lastnameET.text.clear()
            binding.ageET.text.clear()
            binding.genderET.text.clear()
            binding.addressET.text.clear()
            binding.cpnumET.text.clear()

            Toast.makeText(this@UpdateUserProfile, "Successfully Updated Profile data", Toast.LENGTH_SHORT).show()


        }.addOnFailureListener {
            Toast.makeText(this@UpdateUserProfile, "Failed to Update Profile data", Toast.LENGTH_SHORT).show()

        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }


}


