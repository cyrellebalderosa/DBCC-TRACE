package com.example.dbcctrace.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.dbcctrace.databinding.ItemRvNotesBinding
import com.example.dbcctrace.entities.Notes

class NotesAdapter() :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    inner class NotesViewHolder(val binding: ItemRvNotesBinding)
        : RecyclerView.ViewHolder  (binding.root)

        var mListener: OnItemClickListener? = null
        var arrList = ArrayList<Notes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val v = ItemRvNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(v)


    }

    override fun getItemCount(): Int {
        return arrList.size
    }


    fun setData(arrNotesList:List<Notes>){
        arrList = arrNotesList as ArrayList<Notes>


    }

    fun setOnClickListener(listener1:OnItemClickListener){

        mListener = listener1
    }




    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        holder.binding.tvtitle.text = arrList[position].title
        holder.binding.tvdesc.text = arrList[position].noteText
        holder.binding.tvdatetime.text = arrList[position].dateTime

        holder.binding.cardview.setOnClickListener { _ ->
            mListener!!.OnClicked(arrList[position].id!!)
        }




    }


    interface OnItemClickListener : AdapterView.OnItemClickListener {

        fun OnClicked(notesId:Int)

    }



}






