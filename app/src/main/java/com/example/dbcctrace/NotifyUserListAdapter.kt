package com.example.dbcctrace

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class NotifyUserListAdapter(private val usersList:MutableList<NotifyUsers>)
    : RecyclerView.Adapter<NotifyUserListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notify_user_list,
                parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = usersList[position]

        holder.Email.text = user.Email
        holder.Username.text = user.Firstname
        holder.sendbtn.setOnClickListener {

            val builder = AlertDialog.Builder(holder.itemView.context)
            val messageBox: EditText = EditText(holder.itemView.context)

            builder.setTitle("Send Notification")
            builder.setView(messageBox)

            builder.setNegativeButton("CANCEL", null)

            builder.setPositiveButton("SEND") { _, _ ->
                val message = messageBox.text.toString()

                sendNotifications("You have a Message!",
                        message,
                        user.id!!,
                        holder.itemView.context
                )
            }

            builder.show()
        }
    }


    private fun sendNotifications(title: String, text: String, recieverId: String, context: Context){

        val notifications = Notifications(text,title,recieverId)

        FirebaseDatabase
                .getInstance()
                .getReference("Notification")
                .push()
                .setValue(notifications)
                .addOnCompleteListener {  task ->
                    if (task.isSuccessful){

                        Toast.makeText(context, "Message Sent!", Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(context, "Message didn't Sent!", Toast.LENGTH_SHORT).show()

                    }
                }

    }

    override fun getItemCount(): Int {
     return  usersList.size
    }


    class MyViewHolder(view:View): RecyclerView.ViewHolder(view) {

        val Username:TextView = view.findViewById(R.id.tvname)
        val Email:TextView = view.findViewById(R.id.tvemail)
        val sendbtn:Button = view.findViewById(R.id.sendbtn)

    }
}