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
import com.example.tcc.databinding.FragmentAreaManagerBinding
import com.example.tcc.databinding.FragmentVintageManagerBinding
import com.example.tcc.dialogs.AddAreaDialogFragment
import com.example.tcc.dialogs.AddPropertyDialogFragment
import com.example.tcc.dialogs.AddVintageDialogFragment
import com.example.tcc.model.Area
import com.example.tcc.model.Vintage
import com.example.tcc.ui.adapter.EntryManageAreaAdapter
import com.example.tcc.ui.listeners.EntryListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class VintageManagerFragment : Fragment() {

    private val args: VintageManagerFragmentArgs? by navArgs()

    private var _binding: FragmentVintageManagerBinding? = null
    private val binding get() = _binding!!

    //private val entryAdapter = EntryManageAreaAdapter()
    private val vintageList = mutableListOf<Vintage>()

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var dialogAdd: AddVintageDialogFragment

    private val vintage = Vintage(null, null,null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVintageManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        Log.d("db", "ID Talhão: ${args?.areaId}")

        /*binding.rvAreaEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAreaEntries.setHasFixedSize(true)
        binding.rvAreaEntries.adapter = entryAdapter*/

        val listener = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                if (selected) {
                    //vintage.description = vintageList[entryAdapter.positionSelected].description
                    //vintage.begin = vintageList[entryAdapter.positionSelected].begin
                    //vintage.begin = vintageList[entryAdapter.positionSelected].end
                    binding.buttonEdit.visibility = View.VISIBLE
                    binding.buttonGo.visibility = View.VISIBLE
                } else {
                    binding.buttonEdit.visibility = View.INVISIBLE
                    binding.buttonGo.visibility = View.INVISIBLE
                }
            }
        }
        //entryAdapter.attachListener(listener)

        getEntries()

        initClicks()
    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener {
            auth.signOut()
            //findNavController().navigate(R.id.action_areaManagerFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener {
            //findNavController().navigate(R.id.action_areaManagerFragment_to_managerFragment)

        }
        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddVintageDialogFragment(null, args?.areaId)
            dialogAdd.show(childFragmentManager, AddPropertyDialogFragment.TAG)
        }

        binding.buttonEdit.setOnClickListener {
            //dialogAdd = AddVintageDialogFragment(vintageList[entryAdapter.positionSelected], args?.areaId)
            dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)
        }

        binding.buttonGo.setOnClickListener {

            db.collection("Vintage").whereArrayContains("users", auth.currentUser?.uid.toString())
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val documents = snapshot?.documents
                        if (documents != null) {
                            vintageList.clear()

                            for (document in documents) {

                                if (document.get("description") == vintage.description &&
                                    document.get("begin") == vintage.begin &&
                                    document.get("end") == vintage.end
                                ) {
                                    //AVANÇAR NA FRAGMENT
                                }
                            }

                        }
                    }
                }
        }
    }

    private fun getEntries() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}