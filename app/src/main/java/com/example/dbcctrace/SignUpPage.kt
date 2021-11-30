@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.properties.Delegates

@Suppress("DEPRECATION")
class SignUpPage : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivitySignUpPageBinding

    //ActionBar
    private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private var valid by Delegates.notNull<Boolean>()


    private var email = ""
    private var password = ""
    private lateinit var fname: EditText
    private lateinit var lname: EditText
    private lateinit var gender1: EditText
    private lateinit var age1: EditText
    private lateinit var address1: EditText
    private lateinit var phone1: EditText
    private lateinit var userName: EditText
    private lateinit var pass: EditText


    private lateinit var isAdminbox: CheckBox
    private lateinit var isUserbox: CheckBox



     val  IS_ADMIN_KEY = "isAdmin"




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
        fStore = FirebaseFirestore.getInstance()



        userName = findViewById(R.id.emailEt)
        pass = findViewById(R.id.passwordEt)
        fname = findViewById(R.id.firstnameEt)
        lname = findViewById(R.id.lastnameEt)
        gender1 = findViewById(R.id.genderEt)
        age1 = findViewById(R.id.ageEt)
        address1 = findViewById(R.id.addressEt)
        phone1 = findViewById(R.id.numberEt)

        //checkbox
        isAdminbox = findViewById(R.id.asadmin)
        isUserbox = findViewById(R.id.asuser)


        isAdminbox.setOnCheckedChangeListener { compoundButton, _ ->

            if (compoundButton.isChecked){
                isUserbox.isChecked = false
            }
        }


        isUserbox.setOnCheckedChangeListener { compoundButton, _ ->

            if (compoundButton.isChecked){
                isAdminbox.isChecked = false
            }
        }

        //handle click, begin signup
        binding.signUpBtn1.setOnClickListener {


            checkField(fname)
            checkField(lname)
            checkField(gender1)
            checkField(age1)
            checkField(address1)
            checkField(phone1)


            //validate data
            validateData()

            if (!(isAdminbox.isChecked() || isUserbox.isChecked())){
                Toast.makeText(this, "Select the Account type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


        }









    }


    fun saveFireStore() {
        val db = FirebaseFirestore.getInstance()
        val user: MutableMap<String, Any> = HashMap()
        val firebaseUser = firebaseAuth.currentUser


        // Add user admin privileges to 'users' collection in Firestore
        firebaseUser?.let { fStore.collection("users").document(it.uid).set(user) }

        user.put("Username", userName.text.toString())
        user.put("Password", pass.text.toString())
        user.put("Firstname", fname.text.toString())
        user.put("Lastname", lname.text.toString())
        user.put("Age", age1.text.toString())
        user.put("Gender", gender1.text.toString())
        user.put("Address", address1.text.toString())
        user.put("PhoneNum", phone1.text.toString())
        user.put(IS_ADMIN_KEY, "false") // by default a new user is not an admin





        //specify if user is Admin
        if (isAdminbox.isChecked) {
            user.put("isAdmin", "1")
        }

        if (isUserbox.isChecked) {
            user.put("isUser", "1")
        }





        db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Toast.makeText(this@SignUpPage, "record added succesfully", Toast.LENGTH_SHORT).show()

                    if (isAdminbox.isChecked){
                    startActivity(Intent(this, AdminDashboardPage::class.java))
                    finish()
                    }

                    if (isUserbox.isChecked){
                        startActivity(Intent(this, UserDashboardPage::class.java))
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this@SignUpPage, "record failed to add", Toast.LENGTH_SHORT).show()

                }


    }

    fun checkField(textField: EditText): Boolean {
        if (textField.text.toString().isEmpty()) {
            textField.error = "Error"
            valid = false
        } else {
            valid = true
        }
        return valid
    }

    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid emailformat
            binding.emailEt.error = "Invalid email format"
        }
        else if (TextUtils.isEmpty(password)){
            //password isn't entered
            binding.passwordEt.error = "Please enter password"
        }
        else if (password.length <6){
            //password length is less than 6

            binding.passwordEt.error= "Password must be atleast 6 charactrs long"
        }
        else{
            //data is valid, continue to sign up
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        //show progress
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    //signup successful
                    progressDialog.dismiss()
                    //get current user
                    val firebaseUser = firebaseAuth.currentUser
                    val email = firebaseUser!!.email

                    Toast.makeText(this, "Account created with email $email", Toast.LENGTH_SHORT).show()

                    //save data to firestore
                    saveFireStore()

                }
                .addOnFailureListener { e->
                    //signup failed
                    progressDialog.dismiss()
                    Toast.makeText(this, "SignUp Failed due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
    }





    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }


}