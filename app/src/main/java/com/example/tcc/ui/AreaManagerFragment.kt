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
import com.example.tcc.databinding.FragmentManagerBinding
import com.example.tcc.dialogs.AddAreaDialogFragment
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

    private lateinit var dialogAdd: AddAreaDialogFragment

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

        Log.d("db", "ID Propiedade: ${args?.propId}")

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
        binding.ibLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_areaManagerFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener {
            findNavController().navigate(R.id.action_areaManagerFragment_to_managerFragment)
        }
        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddAreaDialogFragment(null, args?.propId)
            dialogAdd.show(childFragmentManager, AddPropertyDialogFragment.TAG)
        }

        binding.buttonEdit.setOnClickListener {
            dialogAdd = AddAreaDialogFragment(areaList[entryAdapter.positionSelected], args?.propId)
            dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)
        }

        binding.buttonGo.setOnClickListener {

            db.collection("Area").whereArrayContains("users", auth.currentUser?.uid.toString())
                .addSnapshotListener { snapshot, e ->
                    if (e == null) {
                        val documents = snapshot?.documents
                        if (documents != null) {
                            areaList.clear()

                            for (document in documents) {

                                if (document.get("name") == area.name &&
                                    document.get("dimension") == area.dimension &&
                                    document.get("crop") == area.crop
                                ) {
                                    navigate(AreaManagerFragmentDirections
                                        .actionAreaManagerFragmentToVintageManagerFragment(document.id, args?.propId))
                                }
                            }

                        }
                    }
                }
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
        db.collection("Area").whereEqualTo("property", args?.propId)
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        areaList.clear()

                        for (document in documents) {
                            val name = document.get("name").toString()
                            val dimension = document.get("dimension")
                            val crop = document.get("crop").toString()
                            val newArea = Area(name, dimension as Long, crop)

                            //Log.d("db", "ID: ${document.id}  DADOS: ${document.data}")

                            areaList.add(newArea)
                            entryAdapter.updateAreas(areaList)
                        }

                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

