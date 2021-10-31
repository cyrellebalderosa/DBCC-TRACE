package com.example.dbcctrace

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.dbcctrace.databinding.ActivityLogInPageBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception


class LogInPage : AppCompatActivity() {


    //viewBinding
    private lateinit var binding: ActivityLogInPageBinding

    //google
    private lateinit var googleSignInClient: GoogleSignInClient

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
    private var email = ""
    private var password = ""

    //Declare facebook auth
    lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"



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
        CheckUser()

        //configure actionbar
        actionBar = supportActionBar!!
        actionBar.title = "Login"


        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Logging In..")
        progressDialog.setCanceledOnTouchOutside(false)


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

        //facebook configure/ handle click
        callbackManager = CallbackManager.Factory.create()

        binding.fbloginBtn.setPermissions("email")
        binding.fbloginBtn.setOnClickListener {
            signIn()
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
        } else {
            //data is validated, begin login
            firebaseLogin()
        }
    }

    private fun firebaseLogin() {
        //show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                progressDialog.dismiss()
                //get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this, "Logged in as $email", Toast.LENGTH_SHORT).show()

                //open profileActivity
                startActivity(Intent(this, DashboardPage::class.java))
                finish()

            }
            .addOnFailureListener { e ->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun CheckUser() {
        //if user is already logged in go to profile activity
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            startActivity(Intent(this, DashboardPage::class.java))
            finish()
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

        callbackManager.onActivityResult(requestCode,resultCode,data)
    }




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

                    //start profile activity
                    startActivity(Intent(this@LogInPage , DashboardPage::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    //login Failed
                    Log.d(TAG, "firebaseAuthwithGoogleAccount: LogIn failed due to ${e.message}")
                    Toast.makeText(this@LogInPage, "LogIn failed due to ${e.message}", Toast.LENGTH_SHORT).show()

                }
        //END auth with google


    }


    //facebook sign in
    private fun signIn() {
        binding.fbloginBtn.setReadPermissions("email", "public_profile")
        binding.fbloginBtn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)

            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {

            }

        })
    }



    private fun handleFacebookAccessToken(accessToken: AccessToken?) {

        //get credential
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = firebaseAuth.currentUser

                        startActivity(Intent(this,DashboardPage::class.java))
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                    }
                }


    }

}


