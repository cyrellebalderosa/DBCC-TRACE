package com.example.dbcctrace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QRresultAdapter(private val qrList:ArrayList<QRdata>)
    : RecyclerView.Adapter<QRresultAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.qr_item,
                parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = qrList[position]

        holder.qrdata.text = currentitem.scannedqr
        holder.qrdate.text = currentitem.date
    }

    override fun getItemCount(): Int {
        return qrList.size
    }

    class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){


        val qrdata: TextView = itemView.findViewById(R.id.qrdataTV)
        val qrdate: TextView = itemView.findViewById(R.id.qrdateTV)
    }
}