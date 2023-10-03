package com.example.tcc.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tcc.R
import com.example.tcc.databinding.FragmentHomeBinding
import com.example.tcc.databinding.FragmentRequestBinding
import com.example.tcc.dialogs.AddPropertyDialogFragment
import com.example.tcc.model.Property
import com.example.tcc.model.Request
import com.example.tcc.ui.adapter.EntryManagePropertyAdapter
import com.example.tcc.ui.adapter.RequestManageReceivedAdapter
import com.example.tcc.ui.adapter.RequestReceivedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var  requestReceiverAdapter : RequestReceivedAdapter
    private lateinit var  requestManageAdapter : RequestManageReceivedAdapter

    private val requestReceiverList = mutableListOf<Request>()
    private val requestManageList = mutableListOf<Request>()

    private lateinit var dialogAdd: AddPropertyDialogFragment

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}