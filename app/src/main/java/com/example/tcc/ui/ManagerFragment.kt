package com.example.tcc.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.R
import com.example.tcc.databinding.FragmentManagerBinding
import com.example.tcc.dialogs.AddPropertyDialogFragment
import com.example.tcc.model.Property
import com.example.tcc.ui.listeners.EntryListener
import com.example.tcc.ui.adapter.EntryManagePropertyAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ManagerFragment : Fragment() {

    private var _binding: FragmentManagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var  entryAdapter : EntryManagePropertyAdapter
    private val propertyList = mutableListOf<Property>()

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var dialogAdd: AddPropertyDialogFragment
    //private var dialogEdit = AddPropertyDialogFragment()

    private val property = Property(null, null, null)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManagerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        getEntries()

        initClicks()

    }

    private fun initAdapter(){
        binding.rvPropertyEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPropertyEntries.setHasFixedSize(true)
        entryAdapter = EntryManagePropertyAdapter { property, select ->
            optionSelect(property,select)
        }
        binding.rvPropertyEntries.adapter = entryAdapter
    }

    private fun optionSelect(property: Property, select: Int) {
        when(select){
            EntryManagePropertyAdapter.SELECT_EDIT -> {
                dialogAdd = AddPropertyDialogFragment(propertyList[entryAdapter.positionSelected])
                dialogAdd.show(childFragmentManager, AddPropertyDialogFragment.TAG)
            }
            EntryManagePropertyAdapter.SELECT_NEXT -> {
                db.collection("Property").whereArrayContains("users", auth.currentUser?.uid.toString())
                    .addSnapshotListener { snapshot, e ->
                        if (e == null) {
                            val documents = snapshot?.documents
                            if (documents != null) {
                                propertyList.clear()

                                for (document in documents) {

                                    if (document.get("name") == property.name &&
                                        document.getDouble("dimension") == property.dimension &&
                                        document.get("localization") == property.location
                                    ) {
                                        navigate(ManagerFragmentDirections.actionManagerFragmentToAreaManagerFragment(document.id))
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
            findNavController().navigate(R.id.action_managerFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener {
            findNavController().navigate(R.id.action_managerFragment_to_homeFragment)

        }
        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddPropertyDialogFragment(null)
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
        db.collection("Property").whereArrayContains("users", auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        propertyList.clear()

                        for (document in documents) {
                            val name = document.get("name").toString()
                            val dimension = document.getDouble("dimension")
                            val localization = document.get("localization").toString()
                            val newProperty = Property(name,dimension as Double , localization)

                            //Log.d("db", "ID: ${document.id}  DADOS: ${document.data}")

                            propertyList.add(newProperty)
                        }
                        initAdapter()
                        entryAdapter.updateProperties(propertyList)
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}