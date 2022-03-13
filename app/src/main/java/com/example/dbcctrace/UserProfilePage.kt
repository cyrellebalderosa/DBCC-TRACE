@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityUserProfilePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class UserProfilePage : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfilePageBinding

    private lateinit var fullnameTv: TextView
    private lateinit var genderTv: TextView
    private lateinit var emailTv: TextView
    private lateinit var ageTv: TextView
    private lateinit var addTv: TextView
    private lateinit var cpnumTv: TextView
    private lateinit var logouttv: TextView
    private lateinit var backbtn: ImageView
    private lateinit var profilepic: ImageView
    private lateinit var editprofile:TextView



    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var dialog: Dialog
    private lateinit var user: UsersDB
    private lateinit var id: String
    private lateinit var username: String
    private lateinit var imageUri: Uri



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullnameTv = findViewById(R.id.fullnametv)
        genderTv = findViewById(R.id.gendertv)
        emailTv = findViewById(R.id.emailtv)
        ageTv = findViewById(R.id.agetv)
        addTv = findViewById(R.id.addresstv)
        cpnumTv = findViewById(R.id.cpnumtv)
        logouttv = findViewById(R.id.logoutTV)
        backbtn = findViewById(R.id.backbtn)
        profilepic = findViewById(R.id.ProfilePic)
        editprofile = findViewById(R.id.editprofile)


        firebaseAuth = FirebaseAuth.getInstance()
        id = firebaseAuth.currentUser?.uid.toString()




        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        if (id.isNotEmpty()){


            getUserData()


        }



        profilepic.setOnClickListener {
            selectImage()
        }


        logouttv.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInPage::class.java))
        }

        backbtn.setOnClickListener {
           onSupportNavigateUp()
        }

        editprofile.setOnClickListener {
            startActivity(Intent(this, UpdateUserProfile::class.java))
        }





    }

    private fun getUserData() {
        showProgressBar()



        databaseReference.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                user = snapshot.getValue(UsersDB::class.java)!!
                binding.fullnametv.setText(user.Firstname)
                binding.gendertv.setText(user.Gender)
                binding.emailtv.setText(user.Email)
                binding.agetv.setText(user.Age)
                binding.addresstv.setText(user.Address)
                binding.cpnumtv.setText(user.PhoneNum)


                getUserProfilePic()
            }

            override fun onCancelled(error: DatabaseError) {
                hideProgressBar()
                Toast.makeText(this@UserProfilePage, "Failed to get User Profile data", Toast.LENGTH_SHORT).show()

            }

        })
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
        binding.ProfilePic.setImageURI(imageUri)

        uploadImage()
        getUserProfilePic()
    }


    private fun uploadImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File.....")
        progressDialog.setCancelable(false)
        progressDialog.show()


         storageReference = FirebaseStorage.getInstance().getReference("image/"+firebaseAuth.currentUser?.uid+".jpg")

        storageReference.putFile(imageUri).addOnSuccessListener {

            Toast.makeText(this@UserProfilePage, "Successfully uploaded", Toast.LENGTH_SHORT).show()

            if (progressDialog.isShowing) progressDialog.dismiss()

            getUserProfilePic()

        }.addOnFailureListener {

            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this@UserProfilePage, "Failed to upload", Toast.LENGTH_SHORT).show()

        }


    }


    private fun getUserProfilePic(){

        storageReference = FirebaseStorage.getInstance().reference.child("image/$id.jpg")
        val localFile = File.createTempFile("tempImage","jpg")
        storageReference.getFile(localFile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.ProfilePic.setImageBitmap(bitmap)
            hideProgressBar()

        }.addOnFailureListener {

            hideProgressBar()
            //Toast.makeText(this@UserProfilePage, "Failed to retrieve image", Toast.LENGTH_SHORT).show()


        }
    }



    private fun  showProgressBar(){

        dialog = Dialog(this@UserProfilePage)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){

        dialog.dismiss()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }
}