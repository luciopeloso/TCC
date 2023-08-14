package com.example.tcc.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.R
import com.example.tcc.databinding.FragmentEntriesManagerBinding
import com.example.tcc.databinding.FragmentVintageManagerBinding
import com.example.tcc.dialogs.AddAreaDialogFragment
import com.example.tcc.dialogs.AddEntriesDialogFragment
import com.example.tcc.dialogs.AddPropertyDialogFragment
import com.example.tcc.dialogs.AddVintageDialogFragment
import com.example.tcc.model.ChildData
import com.example.tcc.model.ParentData
import com.example.tcc.model.Vintage
import com.example.tcc.ui.adapter.EntryManageEntriesAdapter
import com.example.tcc.ui.adapter.EntryManageVintageAdapter
import com.example.tcc.ui.listeners.EntryListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EntriesManagerFragment : Fragment() {

    private val args: EntriesManagerFragmentArgs? by navArgs()

    private var _binding: FragmentEntriesManagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var entryAdapter : EntryManageEntriesAdapter
    private val entryList = mutableListOf<ChildData>()

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var dialogAdd: AddEntriesDialogFragment

    //private val vintage = Vintage(null, null,null)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntriesManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listData: MutableList<ParentData> = ArrayList()

        val parentData: MutableList<String> =
            mutableListOf("Produto",
                "Sementes",
                "Capina, dessecação e pós emergência",
                "Fungicidas, Inseticidas e Foliares",
                "Operações")

        val parentObj1 = ParentData(parentTitle = parentData[0])
        val parentObj2 = ParentData(parentTitle = parentData[1])
        val parentObj3 = ParentData(parentTitle = parentData[2])
        val parentObj4 = ParentData(parentTitle = parentData[3])
        val parentObj5 = ParentData(parentTitle = parentData[4])
        val parentObj6 = ParentData(parentTitle = parentData[5])

        listData.add(parentObj1)
        listData.add(parentObj2)
        listData.add(parentObj3)
        listData.add(parentObj4)
        listData.add(parentObj5)
        listData.add(parentObj6)

        auth = Firebase.auth

        Log.d("db", "ID Talhão: ${args?.vintageId}")

        binding.rvEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntries.setHasFixedSize(true)
        entryAdapter = EntryManageEntriesAdapter(listData)

        val listener = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                if (selected) {
                    /*vintage.description = vintageList[entryAdapter.positionSelected].description
                    vintage.begin = vintageList[entryAdapter.positionSelected].begin
                    vintage.begin = vintageList[entryAdapter.positionSelected].end*/
                    binding.buttonEdit.visibility = View.VISIBLE

                } else {
                    binding.buttonEdit.visibility = View.INVISIBLE

                }
            }
        }
        entryAdapter.attachListener(listener)

        getEntries()

        initClicks()
    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_entriesManagerFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener {
            findNavController().navigate(R.id.action_entriesManagerFragment_to_vintageManagerFragment)
        }

        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddEntriesDialogFragment(null, args?.vintageId)
            dialogAdd.show(childFragmentManager, AddVintageDialogFragment.TAG)
        }

        binding.buttonEdit.setOnClickListener {
            dialogAdd = AddEntriesDialogFragment(entryList[entryAdapter.positionSelected], args?.vintageId)
            dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)
        }
    }

    private fun getEntries() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}