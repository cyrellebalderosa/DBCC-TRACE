package com.example.dbcctrace

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityAdminDeleteUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminDeleteUser : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDeleteUserBinding

    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDeleteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)


       // val uid = firebaseAuth.currentUser?.uid

        binding.deletebtn.setOnClickListener {


            var id = binding.deleteET.text.toString()
            if (id.isNotEmpty())
                deleteData(id)
            else
                Toast.makeText(this, "Please enter id of user", Toast.LENGTH_SHORT).show()
        }
    }


    private fun deleteData(id: String){

        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(id).removeValue().addOnSuccessListener {

            binding.deleteET.text.clear()
            Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show()

        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

            }
    }
}