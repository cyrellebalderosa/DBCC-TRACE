package com.example.dbcctrace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminCloudNotifyPage : AppCompatActivity() {

    private lateinit var adapter: NotifyUserListAdapter
    private lateinit var recyclerView: RecyclerView
    private val  userList: MutableList<NotifyUsers> = mutableListOf()
    private lateinit var dbref : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_cloud_notify_page)


        recyclerView = findViewById(R.id.Notifyuserlist)
        //recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)


        //userList = arrayListOf<NotifyUsers>()
        adapter = NotifyUserListAdapter(userList)

        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(this)

       getAllUser()
    }


    private fun getDBUser(){

        dbref = FirebaseDatabase.getInstance().getReference("users")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){


                        val user = userSnapshot.getValue(NotifyUsers::class.java)
                        user?.let {
                            if(user.id == FirebaseAuth.getInstance().currentUser!!.uid){
                                userList.add(user)
                                adapter.notifyDataSetChanged()
                            }

                        }

                    }

                   // userList.addAll(userArrayList)

                    recyclerView.adapter = NotifyUserListAdapter(userList)



                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }


    private fun getAllUser(){
        FirebaseDatabase
            .getInstance()
            .getReference("users")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user = snapshot.getValue(NotifyUsers::class.java)

                    user?.let {
                        if (user.id != FirebaseAuth.getInstance().currentUser!!.uid){
                            userList.add(user)
                            adapter.notifyDataSetChanged()
                        }


                    }

                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {


                }

                override fun onChildRemoved(snapshot: DataSnapshot) {

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }


}