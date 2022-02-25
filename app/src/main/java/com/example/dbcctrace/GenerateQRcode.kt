package com.example.dbcctrace

import android.os.Bundle
import android.widget.*
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.example.dbcctrace.databinding.ActivityGenerateQRcodeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class GenerateQRcode : AppCompatActivity() {


    private lateinit var binding: ActivityGenerateQRcodeBinding
    var currentDate:String? = null
    private lateinit var datetime: TextView
    private lateinit var tvName: EditText
    private lateinit var tvAddress: EditText
    private lateinit var etTemp: EditText
    private lateinit var tvSeat: TextView
    private lateinit var ivQRCode: ImageView
    private lateinit var btngenerate: Button
    private val STORAGE_CODE = 1001



    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var id: String
    private lateinit var user: UsersDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateQRcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //init
        datetime = findViewById(R.id.Datetv)
        tvName = findViewById(R.id.Nametv)
        tvAddress = findViewById(R.id.addressTV)
        etTemp = findViewById(R.id.etTemp)
        tvSeat = findViewById(R.id.Seattv)
        ivQRCode = findViewById(R.id.ivQRCode)
        btngenerate = findViewById(R.id.btngenerate)



        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        id = firebaseAuth.currentUser?.uid.toString()



        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        databaseReference.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(UsersDB::class.java)!!
                binding.Nametv.setText(user.Firstname +" "+ user.Lastname)
                binding.addressTV.setText(user.Address)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GenerateQRcode, "Failed to get User Profile data", Toast.LENGTH_SHORT).show()
            }

        })



        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        currentDate = sdf.format(Date())


        binding.Datetv.text = currentDate








        btngenerate.setOnClickListener {

           val temp = etTemp.text.toString().trim()
            val name = tvName.text.toString().trim()
            val address = tvAddress.text.toString().trim()


            val encodertemp = QRGEncoder("FullName: "+ name+ "\n"+ "temperature:"  +temp  + "\n"+ "Address: "+ "\n"+address + "\n"+ currentDate, QRGContents.Type.TEXT, 800)
            binding.ivQRCode.setImageBitmap(encodertemp.bitmap)


        }








    }



}


