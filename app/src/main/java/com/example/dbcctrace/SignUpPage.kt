@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

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

    private lateinit var storageReference: StorageReference
    private lateinit var dialog: Dialog
    private lateinit var user: UsersDB
    private lateinit var id: String
    private lateinit var username: String
    private lateinit var imageUri: Uri
    private lateinit var signupProfilePic: ImageView

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
        id = firebaseAuth.currentUser?.uid.toString()

        userName = findViewById(R.id.emailEt)
        pass = findViewById(R.id.passwordEt)
        fname = findViewById(R.id.firstnameEt)
        lname = findViewById(R.id.lastnameEt)

        age1 = findViewById(R.id.ageEt)
        address1 = findViewById(R.id.addressEt)
        phone1 = findViewById(R.id.contactEt)
        gender = findViewById(R.id.genderEt)
        signupProfilePic = findViewById(R.id.signupProfilePic)

        //handle click, begin signup

        binding.signUpBtn1.setOnClickListener {

            firebaseSignUp()
        }

        signupProfilePic.setOnClickListener {
            selectImage()
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

    private fun selectImage(){

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode ==  RESULT_OK)

            imageUri = data?.data!!
            binding.signupProfilePic.setImageURI(imageUri)

        //uploadImage()
    }

    private fun uploadImage(){

        storageReference = FirebaseStorage.getInstance().getReference("image/"+firebaseAuth.currentUser?.uid+".jpg")

        storageReference.putFile(imageUri).addOnSuccessListener {

            Toast.makeText(this@SignUpPage, "Successfully uploaded", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {

            Toast.makeText(this@SignUpPage, "Failed to upload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun  showProgressBar(){

        dialog = Dialog(this@SignUpPage)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
    private fun hideProgressBar(){

        dialog.dismiss()
    }
    private fun showRegularUI() {
        // Switches to admin (organizer) UI
        val intent = Intent(this@SignUpPage, UserDashboardPage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // clears the stack (disables going back with back button)
        startActivity(intent)
        uploadImage()
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
