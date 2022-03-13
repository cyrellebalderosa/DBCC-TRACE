@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging


class SignUpPage : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivitySignUpPageBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference


    private var email = ""
    private var password = ""
    private lateinit var fname: EditText
    private lateinit var lname: EditText
    private lateinit var age1: EditText
    private lateinit var address1: EditText
    private lateinit var phone1: EditText
    private lateinit var userName: EditText
    private lateinit var pass: EditText
    private lateinit var gender: EditText








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //configure actionbar,    //enable back button
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //configure progressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating account...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("users")




        userName = findViewById(R.id.emailEt)
        pass = findViewById(R.id.passwordEt)
        fname = findViewById(R.id.firstnameEt)
        lname = findViewById(R.id.lastnameEt)

        age1 = findViewById(R.id.ageEt)
        address1 = findViewById(R.id.addressEt)
        phone1 = findViewById(R.id.contactEt)
        gender = findViewById(R.id.genderEt)


        //handle click, begin signup

        binding.signUpBtn1.setOnClickListener {
            firebaseSignUp()
        }




    }




    private fun signUp(){
        var Firstname = fname.text.toString().trim()
        var Lastname = lname.text.toString().trim()
        var Age = age1.text.toString().trim()
        var Address = address1.text.toString().trim()
        var PhoneNum = phone1.text.toString().trim()
        var Email = userName.text.toString().trim()
        var Password = pass.text.toString().trim()
        var Gender = gender.text.toString().trim()

        val uid = firebaseAuth.currentUser?.uid

        if (userName.text.toString() == "dbcc.trace@gmail.com" && pass.text.toString() == "traceadmin"){
            showAdminUI()
        }else{
            showRegularUI()
        }



        if (Email.isEmpty() || Password.isEmpty() || Firstname.isEmpty() || Lastname.isEmpty()){
            Toast.makeText(this@SignUpPage, "All Fields Required", Toast.LENGTH_SHORT).show()
        }else {

            if (isValidEmail(Email)) {

                val userId = FirebaseAuth.getInstance().currentUser!!.uid

                if (uid != null) {
                    var model = UsersDB(Firstname, Lastname, Age, Address, PhoneNum, Email, Password, Gender, userId)


                    //DATA INSERT HERE TO DATABASE
                    databaseReference.child(uid).setValue(model)
                    Toast.makeText(this@SignUpPage, "SignUp successfull", Toast.LENGTH_SHORT).show()


                    showRegularUI()

                } else {
                    Toast.makeText(this@SignUpPage, "Check you email", Toast.LENGTH_SHORT).show()

                }
            }
        }

    }

    private fun isValidEmail(email: String): Boolean{
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }


    private fun firebaseSignUp() {
        //show progress
        progressDialog.show()

        var Email = userName.text.toString().trim()
        var Password = pass.text.toString().trim()



        //create account
        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnSuccessListener {
                    //signup successful
                    progressDialog.dismiss()


                    if (userName.text.toString() == "dbcc.trace@gmail.com" && pass.text.toString() == "traceadmin"){
                        showAdminUI()
                    }else{
                        showRegularUI()
                    }


                    //get current user
                    val firebaseUser = firebaseAuth.currentUser
                    val email = firebaseUser!!.email

                    Toast.makeText(this, "Account created with email $email", Toast.LENGTH_SHORT).show()

                    //save data to firebase database
                    signUp()



                    //Firebase messageing Token
                    retrieveAndSToreToken()

                }
                .addOnFailureListener { e->
                    //signup failed
                    progressDialog.dismiss()
                    Toast.makeText(this, "SignUp Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun showRegularUI() {
        // Switches to admin (organizer) UI
        val intent = Intent(this@SignUpPage, UserDashboardPage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // clears the stack (disables going back with back button)
        startActivity(intent)
    }
    private fun showAdminUI() {
        // Switches to admin (organizer) UI
        val intent = Intent(this@SignUpPage, AdminDashboardPage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // clears the stack (disables going back with back button)
        startActivity(intent)
    }

    private fun retrieveAndSToreToken(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->

                if(task.isSuccessful){
                    val token = task.result

                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    FirebaseDatabase.getInstance()
                        .getReference("tokens")
                        .child(uid)
                        .setValue(token)
                }
            }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }


}