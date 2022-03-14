package com.example.dbcctrace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecordsAdapter(private val userlist : ArrayList<UserItemsDb>)
    : RecyclerView.Adapter<RecordsAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item_records,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = userlist[position]

        holder.FirstName.text = currentitem.Firstname
        holder.LastName.text = currentitem.Lastname
        holder.Age.text = currentitem.Age
        holder.Address.text = currentitem.Address
        holder.PhoneNum.text = currentitem.PhoneNum
        //holder.id.text = currentitem.id

    }

    override fun getItemCount(): Int {
        return userlist.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val FirstName : TextView = itemView.findViewById(R.id.tvfirstName)
        val LastName : TextView = itemView.findViewById(R.id.tvlastName)
        val Age : TextView = itemView.findViewById(R.id.tvage)
        val Address : TextView = itemView.findViewById(R.id.tvaddress)
        val PhoneNum : TextView = itemView.findViewById(R.id.tvphonenum)
        //val id : TextView = itemView.findViewById(R.id.tvid)

    }
}