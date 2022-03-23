@file:Suppress("DEPRECATION")

package com.example.dbcctrace

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.util.*

class AdminRecordsPage : AppCompatActivity(),
        RecordsAdapter.onItemClickListener {

    private lateinit var dbref : DatabaseReference
    private lateinit var userRecyclerview : RecyclerView
    private  var userArrayList : ArrayList<UserItemsDb> = arrayListOf<UserItemsDb>()
   // private lateinit var tempArrayList: ArrayList<UserItemsDb>
    private lateinit var dialog: Dialog
    private val adapter = RecordsAdapter(userArrayList, this)



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

        //userArrayList = arrayListOf<UserItemsDb>()
        //tempArrayList = arrayListOf<UserItemsDb>()
        getUserData()

        userRecyclerview.setOnLongClickListener {
            return@setOnLongClickListener false
        }
    }

/**
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
*/

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

                    //tempArrayList.addAll(userArrayList)

                    userRecyclerview.adapter = adapter

                    hideProgressBar()


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })




    }

    private fun showUpdateDialog(id: String?, Firstname: String?){
        val mDialog = AlertDialog.Builder(this)
        mDialog.setTitle("Updating " + Firstname +" Data")
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.update_dialog, null)
        mDialog.setView(view)

        val updateFirstnameET = view.findViewById<EditText>(R.id.updatefirstnameET)
        val updatelastnameET = view.findViewById<EditText>(R.id.updatelastnameET)
        val updateageET = view.findViewById<EditText>(R.id.updateageET)
        val updategenderET = view.findViewById<EditText>(R.id.updategenderET)
        val updateaddressET = view.findViewById<EditText>(R.id.updateaddressET)
        val updatecpnumET = view.findViewById<EditText>(R.id.updatecpnumET)
        val updatebtn = view.findViewById<Button>(R.id.updatebtn)
        val deletebtn = view.findViewById<Button>(R.id.deletebtn)

        val alertDialog: AlertDialog = mDialog.create()
        alertDialog.show()

        deletebtn.setOnClickListener {
            deleteRecord(id)

            alertDialog.dismiss()


        }


        updatebtn.setOnClickListener {
            //update data in database

            val firstname = updateFirstnameET.text.toString()
            val lastname = updatelastnameET.text.toString()
            val age = updateageET.text.toString()
            val gender = updategenderET.text.toString()
            val address = updateaddressET.text.toString()
            val cpnum = updatecpnumET.text.toString()


                updateData(id!!,firstname,lastname,age,gender,address,cpnum)


            Toast.makeText(this,"record updated", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()


        }

    }

    private fun deleteRecord(id: String?) {
        dbref = FirebaseDatabase.getInstance().getReference("users").child(id!!)

        val task = dbref.removeValue()
                task.addOnSuccessListener {
                    Toast.makeText(this,"record deleted", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
                    Toast.makeText(this,"failed to delete", Toast.LENGTH_SHORT).show()

                }


    }

    private fun updateData(id: String, firstname: String, lastname: String, age: String, gender: String, address: String, cpnum: String){


        dbref = FirebaseDatabase.getInstance().getReference("users")

        val updateduser = mapOf<String,String>(
                "firstname" to firstname,
                "lastname" to lastname,
                "age" to age,
                "gender" to gender,
                "address" to address,
                "phoneNum" to cpnum
        )
        dbref.child(id).updateChildren(updateduser).addOnCompleteListener {
            Toast.makeText(this,"record updated", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(this,"failed to update", Toast.LENGTH_SHORT).show()

        }
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

    override fun onItemClick(position: Int): Boolean {

        Toast.makeText(this,"item click $position", Toast.LENGTH_SHORT).show()

        val user = userArrayList[position]
        showUpdateDialog(user.id, user.Firstname)
        return false


    }
}