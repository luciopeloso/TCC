package com.example.tcc.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.tcc.R
import com.example.tcc.databinding.FragmentAreaManagerBinding
import com.example.tcc.databinding.FragmentManagerBinding
import com.example.tcc.dialogs.AddPropertyDialogFragment
import com.example.tcc.model.Property
import com.example.tcc.ui.adapter.EntryManagePropertyAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AreaManagerFragment : Fragment() {

    private val args: AreaManagerFragmentArgs? by navArgs()

    private var _binding: FragmentAreaManagerBinding? = null
    private val binding get() = _binding!!

    private val entryAdapter = EntryManagePropertyAdapter()
    private val areaList = mutableListOf<Property>()

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var dialogAdd: AddPropertyDialogFragment

    private val area = Property(null, null, null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAreaManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        Log.d("db", "ID Propiedade: ${args?.propertyId}")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

