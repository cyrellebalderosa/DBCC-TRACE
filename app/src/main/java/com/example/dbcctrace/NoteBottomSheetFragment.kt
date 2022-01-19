package com.example.dbcctrace

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.dbcctrace.databinding.FragmentNotesBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoteBottomSheetFragment: BottomSheetDialogFragment(){

    var selectedColor = "#171C26"

    companion object {
        var noteId = -1
        fun newInstance(id: Int): NoteBottomSheetFragment {
            val args = Bundle()
            val fragment = NoteBottomSheetFragment()
            fragment.arguments = args
            noteId = id
            return fragment
        }
    }

    private var _binding: FragmentNotesBottomBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentNotesBottomBinding.inflate(inflater, container, false)
        val view = binding.root
        return view   }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (noteId != -1){
            binding.layoutDeleteNote.visibility = View.VISIBLE
        }else{
            binding.layoutDeleteNote.visibility = View.GONE
        }
        setListener()
    }


    private fun setListener(){

        binding.layoutDeleteNote.setOnClickListener {
            val intent = Intent("bottom_sheet_action")
            intent.putExtra("action","DeleteNote")
            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            dismiss()
        }

    }
}