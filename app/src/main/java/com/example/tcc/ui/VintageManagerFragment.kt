package com.example.tcc.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
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
import com.example.tcc.ui.adapter.EntryManageVintageAdapter
import com.example.tcc.ui.listeners.EntryListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class VintageManagerFragment : Fragment() {

    private val args: VintageManagerFragmentArgs? by navArgs()

    private var _binding: FragmentVintageManagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var entryAdapter : EntryManageVintageAdapter
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

        getAreaName()

        getEntries()

        initClicks()
    }

    private fun initAdapter(){
        binding.rvVintageEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVintageEntries.setHasFixedSize(true)
        entryAdapter = EntryManageVintageAdapter() { vintage, select ->
            optionSelect(vintage, select)
        }
        binding.rvVintageEntries.adapter = entryAdapter
    }

    private fun getAreaName(){
        db.collection("Property").document(args?.propId!!)
            .get().addOnSuccessListener { documentProp ->
                val nameProp = documentProp.get("name").toString()
                db.collection("Area").document(args?.areaId!!).get().addOnSuccessListener { documentArea ->
                    val nameArea = documentArea.get("name").toString()
                    binding.textNavigation.text = "Propriedade: $nameProp \nTalhão: $nameArea"
                }
            }
    }

    private fun optionSelect(vintage: Vintage, select: Int) {
        when(select){
            EntryManageVintageAdapter.SELECT_EDIT -> {
                dialogAdd = AddVintageDialogFragment(vintageList[entryAdapter.positionSelected], args?.areaId)
                dialogAdd.show(childFragmentManager, AddVintageDialogFragment.TAG)
            }
            EntryManageVintageAdapter.SELECT_NEXT -> {
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
                                        navigate(VintageManagerFragmentDirections
                                            .actionVintageManagerFragmentToEntriesManagerFragment(document.id,args?.areaId,args?.propId))
                                    }
                                }

                            }
                        }
                    }
            }
        }
    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_vintageManagerFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener {
            navigate(VintageManagerFragmentDirections
                .actionVintageManagerFragmentToAreaManagerFragment(args?.propId))
        }

        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddVintageDialogFragment(null, args?.areaId)
            dialogAdd.show(childFragmentManager, AddPropertyDialogFragment.TAG)
        }
    }

    private fun Fragment.navigate(directions: NavDirections) {
        val controller = findNavController()
        val currentDestination = (controller.currentDestination as? FragmentNavigator.Destination)?.className
            ?: (controller.currentDestination as? DialogFragmentNavigator.Destination)?.className
        if (currentDestination == this.javaClass.name) {
            controller.navigate(directions)
        }
    }

    private fun getEntries() {
        db.collection("Vintage").whereEqualTo("area", args?.areaId)
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        vintageList.clear()
                        for (document in documents) {
                            val description = document.get("description").toString()
                            val begin = document.get("begin").toString()
                            val end = document.get("end").toString()
                            val newVintage = Vintage(description, begin, end)
                            vintageList.add(newVintage)
                        }
                        initAdapter()
                        entryAdapter.updateVintages(vintageList)
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}