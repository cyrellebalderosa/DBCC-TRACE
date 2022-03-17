@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityLogInPageBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION", "NAME_SHADOWING")
class LogInPage : AppCompatActivity() {


    //viewBinding
    private lateinit var binding: ActivityLogInPageBinding

    //google
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var gso:GoogleSignInOptions

    //constants
    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    //ActionBar
    private lateinit var actionBar: ActionBar


    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog


    //Declare FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    //private lateinit var fStore: FirebaseFirestore
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private var email = ""
    private var password = ""

    //Declare facebook auth
    lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"

    private lateinit var userName: EditText
    private lateinit var pass: EditText







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        //initialize firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        //fStore = FirebaseFirestore.getInstance()

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("DBCC").child("users")

        //CheckUser()

        //configure actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Login"


        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In..")
        progressDialog.setCanceledOnTouchOutside(false)


        userName = findViewById(R.id.emailEt)
        pass = findViewById(R.id.passwordEt)


        //handle click, to begin google SignIn
        binding.googleSignInBtn.setOnClickListener {
            //begin google signIn
            Log.d(TAG, "onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)


        }

        //handle click, open SignUpActivity
        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, SignUpPage::class.java))
        }

        //handle click, begin login
        binding.loginBtn.setOnClickListener {

            //before logging in, validate data
           validateData()


        }


        binding.forgotpass.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            builder.setTitle("(Reset Password link will be sent to your Email)")
            val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
            val username = view.findViewById<EditText>(R.id.et_username)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _ ->
                forgotPassword(username)
            })
            builder.setNegativeButton("Close", DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        }

        //facebook configure/ handle click
        callbackManager = CallbackManager.Factory.create()

        binding.fbloginBtn.setPermissions("email")
        binding.fbloginBtn.setReadPermissions("email","public_profile")
        binding.fbloginBtn.registerCallback(callbackManager, object :FacebookCallback<LoginResult>{
            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }

            override fun onSuccess(result: LoginResult) {
                handleFacebookAccessToken(result.accessToken)
            }

        })




    }




    private fun forgotPassword(username : EditText) {


        if (username.text.toString().isEmpty()) {
            return
                }

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()) {
            return
        }

        firebaseAuth.sendPasswordResetEmail(username.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Email Sent!",
                                Toast.LENGTH_SHORT).show()                    }
                }

    }

    //start on activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                firebaseAuthwithGoogleAccount(account)
            } catch (e: Exception) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "onActivityResult: ${e.message}")
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    firebaseAuth.currentUser

                    showRegularUI()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }




    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEt.error = "Invalid email format"
        } else if (TextUtils.isEmpty(password)) {
            //no password entered
            binding.passwordEt.error = "Please enter password"
        }

        else {
            //data is validated, begin login
            login()
        }
    }



    private fun login(){

        var Email = userName.text.toString().trim()
        var Password = pass.text.toString().trim()



        if (Email.isEmpty() || Password.isEmpty() ){
            Toast.makeText(this, "All Fields Required", Toast.LENGTH_SHORT).show()
        }else {

            if (isValidEmail(Email)) {

                isEmailExist(email, password)
                firebaseLogin()

            }else{
                Toast.makeText(this, "Check you email", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun isEmailExist(email: String, password: String)
    {

        databaseReference.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(datasnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.




                var list = ArrayList<UsersDB>()
                var isemailexist = false
                for(postsnapshot in datasnapshot.children ){

                    val value = postsnapshot.getValue<UsersDB>()

                    if (value!!.Email == email && value.Password == password)
                    {
                        isemailexist = true
                    }

                    list.add(value)
                }



                if (isemailexist)
                {
                    Toast.makeText(this@LogInPage, "LogIn Successfully", Toast.LENGTH_SHORT).show()
                    showRegularUI()
                }

            }

            override fun onCancelled(error: DatabaseError) {
               Log.w(TAG,"Failed to read value", error.toException())
            }

        })
    }





    private fun isValidEmail(email: String): Boolean{
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }



    private fun firebaseLogin() {
        //show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { _ ->
                    //login success
                    progressDialog.dismiss()

                    //get user info
                    if (userName.text.toString().trim() == "dbcc.trace@gmail.com" && pass.text.toString().trim() == "traceadmin"){
                        showAdminUI()
                    }else{
                        showRegularUI()

                    }

                    val user = firebaseAuth.currentUser
                    val email = user!!.email

                    Toast.makeText(this, " $email Logged in Successfully", Toast.LENGTH_SHORT).show()

                    //Firebase messageing Token
                    retrieveAndSToreToken()


                }
                .addOnFailureListener { e ->
                    //login failed
                    progressDialog.dismiss()
                    Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
    }




    private fun showAdminUI() {
        // Switches to admin (organizer) UI
        val intent = Intent(this@LogInPage, AdminDashboardPage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // clears the stack (disables going back with back button)
        startActivity(intent)
    }


    private fun showRegularUI() {
        // Switches to admin (organizer) UI
        val intent = Intent(this@LogInPage, UserDashboardPage::class.java)
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


/**
    private fun CheckUser() {
       // if user is already logged in go to profile activity
        //get current user
       val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is already logged in

            if (userName.text.toString() == "dbcc.trace@gmail.com" && pass.text.toString() == "traceadmin"){
                showAdminUI()
            }else{
                showRegularUI()
            }
        }
    }
*/



    // START auth with google
    private fun firebaseAuthwithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthwithGoogleAccount: begin firebase auth with google account")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->

                    //logIn success
                    Log.d(TAG, "firebaseAuthwithGoogleAccount: LoggedIn")

                    //get loggedIN user
                    val firebaseUser = firebaseAuth.currentUser

                    //get user info
                    val uid = firebaseUser!!.uid
                    val email = firebaseUser.email

                    Log.d(TAG, "firebaseAuthwithGoogleAccount: Uid: $uid")
                    Log.d(TAG, "firebaseAuthwithGoogleAccount: Email $email")

                    //check if user is new or existing
                    if (authResult.additionalUserInfo!!.isNewUser){
                        //user is new - Account created
                        Log.d(TAG, "firebaseAuthwithGoogleAccount: Account Created... \n$email")
                        Toast.makeText(this@LogInPage, "Account created... \n $email", Toast.LENGTH_SHORT).show()


                    }

                    else{
                        //existing user LoggedIn
                        Log.d(TAG, "firebaseAuthwithGoogleAccount: Existing user... \n$email")
                        Toast.makeText(this@LogInPage, "LoggedIn... \n $email", Toast.LENGTH_SHORT).show()

                    }
                    //start dashboard activity
                    startActivity(Intent(this@LogInPage , UserDashboardPage::class.java))
                    finish()

                }
                .addOnFailureListener { e ->
                    //login Failed
                    Log.d(TAG, "firebaseAuthwithGoogleAccount: LogIn failed due to ${e.message}")
                    Toast.makeText(this@LogInPage, "LogIn failed due to ${e.message}", Toast.LENGTH_SHORT).show()

                }
        //END auth with google


    }










    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }

}


