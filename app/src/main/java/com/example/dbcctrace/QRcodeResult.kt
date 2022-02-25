package com.example.dbcctrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dbcctrace.databinding.ActivityQrCodeResultBinding
import com.google.firebase.database.*

class QRcodeResult : AppCompatActivity() {

    private lateinit var binding: ActivityQrCodeResultBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var qrRecyclerview: RecyclerView
    private lateinit var qrArrayList : ArrayList<QRdata>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qrRecyclerview = findViewById(R.id.recyclerQRresult)
        qrRecyclerview.layoutManager = LinearLayoutManager(this)
        qrRecyclerview.setHasFixedSize(true)

        qrArrayList = arrayListOf<QRdata>()

        getQRdata()

    }

    private fun getQRdata() {



        dbref = FirebaseDatabase.getInstance().getReference("QRdata")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){


                        val user = userSnapshot.getValue(QRdata::class.java)
                        qrArrayList.add(user!!)
                    }

                    qrRecyclerview.adapter = QRresultAdapter(qrArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}