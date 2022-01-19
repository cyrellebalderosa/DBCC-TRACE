@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


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
    private lateinit var fStore: FirebaseFirestore

    private var email = ""
    private var password = ""

    //Declare facebook auth
    lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"



    val  IS_ADMIN_KEY = "isAdmin"
    val FCM_TOKEN = "fcmToken"





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
        fStore = FirebaseFirestore.getInstance()
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
            firebaseLogin()
        }
    }


    private fun firebaseLogin() {
        //show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { _ ->
                    //login success
                    progressDialog.dismiss()



                    //get user info

                   val firebaseUser = firebaseAuth.currentUser
                   val email = firebaseUser!!.email

                    Toast.makeText(this, " $email Logged in Successfully", Toast.LENGTH_SHORT).show()

                    //checkuser user access level function

                    checkUserAccessLevel(null)


                }
                .addOnFailureListener { e ->
                    //login failed
                    progressDialog.dismiss()
                    Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun checkUserAccessLevel(user : FirebaseUser?) {
        var reference: DocumentReference = fStore.collection("users").document(user!!.uid)
        reference.get().addOnSuccessListener { documentSnapshot ->
            Log.d("TAG", "onSuccess: " + documentSnapshot.data)

            reference = fStore.collection("users").document(user.uid)
            reference.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot: DocumentSnapshot = task.result
                    if (Objects.requireNonNull(documentSnapshot.getString(IS_ADMIN_KEY)).equals("true")) {
                        showAdminUI()// Show admin UI if user is an admin
                    } else {
                        generateAndSaveFCMToken(user) // generate new token for users only when signing in and creating a new account
                        showRegularUI()// Show user UI if user is a regular user
                    }

                }
                else {
                    Log.d(TAG, "Getting user document failed: ", task.exception)
                }
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, " LogIn failed due to ${e.message}")
            Toast.makeText(this@LogInPage, e.message, Toast.LENGTH_SHORT).show() }
    }





    private fun generateAndSaveFCMToken(user: FirebaseUser) {
        // Generates the FCM (Firebase Cloud Messaging) registration token and stores in a document associated with the user
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener(object : OnCompleteListener<String?> {
                    override fun onComplete(task: Task<String?>) {
                        if (!task.isSuccessful) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                            return
                        }
                        val token: String = task.result!! // Get new FCM registration token
                        val userToken: MutableMap<String, Any> = HashMap()
                        userToken[FCM_TOKEN] = token
                        fStore.collection("users").document(user.uid).update(userToken) // Add token to Firestore
                    }
                })
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

    private fun CheckUser() {
       // if user is already logged in go to profile activity
        //get current user
       val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is already logged in

            startActivity(Intent(this, UserDashboardPage::class.java))
            finish()
        }
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


