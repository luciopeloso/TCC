package com.example.tcc

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.tcc.databinding.FragmentAddAreaDialogBinding
import com.example.tcc.databinding.FragmentEntriesBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddDialogFragment(type: String) : DialogFragment() {

    private var _binding: FragmentAddAreaDialogBinding? = null
    private val binding get() = _binding!!
    private  val db = FirebaseFirestore.getInstance()
    private val _type: String = type

    companion object {
        const val TAG = "addAreaDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAreaDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textTitle.text = "Adicionar $_type"
        initClicks()
    }

    private fun initClicks() {
        binding.buttonBack.setOnClickListener{
            dismiss()
        }
    }
}