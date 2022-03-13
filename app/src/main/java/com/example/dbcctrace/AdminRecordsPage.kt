@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.Window
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*

class AdminRecordsPage : AppCompatActivity(){

    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private lateinit var userArrayList : ArrayList<UserItemsDb>
    private lateinit var tempArrayList: ArrayList<UserItemsDb>
    private lateinit var dialog: Dialog


    //ActionBar
    private lateinit var actionBar: ActionBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_records_page)


        //configure actionbar
        actionBar = supportActionBar!!
        actionBar.title = "All User Records"


        userRecyclerview = findViewById(R.id.userlist)
        userRecyclerview.layoutManager = LinearLayoutManager(this)
        userRecyclerview.setHasFixedSize(true)

        userArrayList = arrayListOf<UserItemsDb>()
        tempArrayList = arrayListOf<UserItemsDb>()
        getUserData()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_item, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                tempArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){

                   userArrayList.forEach {

                       if (it.Firstname!!.toLowerCase(Locale.getDefault()).contains(searchText)){

                           tempArrayList.add(it)
                       }
                   }


                    userRecyclerview.adapter!!.notifyDataSetChanged()
                }else{

                    tempArrayList.clear()
                    tempArrayList.addAll(userArrayList)
                    userRecyclerview.adapter!!.notifyDataSetChanged()
                }

                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }


    private fun getUserData() {

        showProgressBar()

        dbref = FirebaseDatabase.getInstance().getReference("users")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){


                        val user = userSnapshot.getValue(UserItemsDb::class.java)
                        userArrayList.add(user!!)

                    }

                    tempArrayList.addAll(userArrayList)

                    userRecyclerview.adapter = RecordsAdapter(tempArrayList)

                    hideProgressBar()


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    private fun  showProgressBar(){

        dialog = Dialog(this@AdminRecordsPage)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hideProgressBar(){

        dialog.dismiss()
    }
}