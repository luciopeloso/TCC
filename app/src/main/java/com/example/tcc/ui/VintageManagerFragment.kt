package com.example.tcc.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.tcc.R
import com.example.tcc.databinding.FragmentAreaManagerBinding
import com.example.tcc.databinding.FragmentVintageManagerBinding
import com.example.tcc.dialogs.AddAreaDialogFragment
import com.example.tcc.model.Area
import com.example.tcc.ui.adapter.EntryManageAreaAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VintageManagerFragment : Fragment() {

    private val args: AreaManagerFragmentArgs? by navArgs()

    private var _binding: FragmentVintageManagerBinding? = null
    private val binding get() = _binding!!

    //private val entryAdapter = EntryManageAreaAdapter()
    //private val areaList = mutableListOf<Area>()

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    //private lateinit var dialogAdd: AddAreaDialogFragment

    //private val area = Area(null, null, null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVintageManagerBinding.inflate(inflater, container, false)
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