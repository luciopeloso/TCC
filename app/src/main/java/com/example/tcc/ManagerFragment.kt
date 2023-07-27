package com.example.tcc

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.databinding.FragmentManagerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ManagerFragment : Fragment() {

    private var _binding: FragmentManagerBinding? = null
    private val binding get() = _binding!!

    private val entryAdapter = EntryManagePropertyAdapter()
    private val propertyList = mutableListOf<Property>()

    private lateinit var auth: FirebaseAuth
    private  val db = FirebaseFirestore.getInstance()

    private lateinit var dialogAdd : AddPropertyDialogFragment
    //private var dialogEdit = AddPropertyDialogFragment()


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

        binding.rvPropertyEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPropertyEntries.setHasFixedSize(true)
        binding.rvPropertyEntries.adapter = entryAdapter

        val listener = object : EntryListener{
            override fun onListClick(selected: Boolean) {
                if(selected){
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
        binding.ibLogout.setOnClickListener{
            auth.signOut()
            findNavController().navigate(R.id.action_manageEntriesFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener{
            findNavController().navigate(R.id.action_manageEntriesFragment_to_homeFragment)

        }
        binding.buttonAdd.setOnClickListener{
            dialogAdd = AddPropertyDialogFragment(null)
            dialogAdd.show(childFragmentManager, AddPropertyDialogFragment.TAG)
        }

        binding.buttonEdit.setOnClickListener {
            //val name = propertyList[entryAdapter.positionSelected].name
            //val dimension = propertyList[entryAdapter.positionSelected].dimension

            dialogAdd = AddPropertyDialogFragment(propertyList[entryAdapter.positionSelected])
            dialogAdd.show(childFragmentManager, AddPropertyDialogFragment.TAG)
        }
    }

    private fun getEntries(){
        db.collection("Property").whereArrayContains("users", auth.currentUser?.uid.toString())
            .addSnapshotListener{ snapshot,e ->
                if(e==null){
                    val documents = snapshot?.documents
                    if(documents != null){
                        propertyList.clear()

                        for(document in documents){
                            val name = document.get("name").toString()
                            val dimension = document.get("dimension")
                            val localization = document.get("localization").toString()
                            val newProperty =  Property(name, dimension as Long,localization)

                            //Log.d("db", "ID: ${document.id}  DADOS: ${document.data}")

                            propertyList.add(newProperty)
                            entryAdapter.updateProperties(propertyList)
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