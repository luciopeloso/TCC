package com.example.tcc.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.R
import com.example.tcc.databinding.FragmentAreaManagerBinding
import com.example.tcc.databinding.FragmentManagerBinding
import com.example.tcc.dialogs.AddPropertyDialogFragment
import com.example.tcc.model.Area
import com.example.tcc.model.Property
import com.example.tcc.ui.adapter.EntryManageAreaAdapter
import com.example.tcc.ui.adapter.EntryManagePropertyAdapter
import com.example.tcc.ui.listeners.EntryListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class AreaManagerFragment : Fragment() {

    private val args: AreaManagerFragmentArgs? by navArgs()

    private var _binding: FragmentAreaManagerBinding? = null
    private val binding get() = _binding!!

    private val entryAdapter = EntryManageAreaAdapter()
    private val areaList = mutableListOf<Area>()

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var dialogAdd: AddPropertyDialogFragment

    private val area = Area(null, null, null)

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

        binding.rvAreaEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAreaEntries.setHasFixedSize(true)
        binding.rvAreaEntries.adapter = entryAdapter

        val listener = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                if (selected) {
                    area.name = areaList[entryAdapter.positionSelected].name
                    area.dimension = areaList[entryAdapter.positionSelected].dimension
                    area.crop = areaList[entryAdapter.positionSelected].crop
                    binding.buttonEdit.visibility = View.VISIBLE
                    binding.buttonGo.visibility = View.VISIBLE
                } else {
                    binding.buttonEdit.visibility = View.INVISIBLE
                    binding.buttonGo.visibility = View.INVISIBLE
                }
            }
        }
        entryAdapter.attachListener(listener)

        getEntries()

        initClicks()
    }

    private fun initClicks() {
        TODO("Not yet implemented")
    }

    private fun getEntries() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

