package com.example.tcc.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.model.ParentData
import com.example.tcc.R
import com.example.tcc.databinding.FragmentEntriesBinding
import com.example.tcc.model.ChildData
import com.example.tcc.ui.adapter.EntriesAdapter
import com.example.tcc.ui.adapter.EntryManageEntriesAdapter
import com.example.tcc.ui.listeners.EntryListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EntriesFragment : Fragment() {

    private var _binding: FragmentEntriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private val entryAccomplishedList = mutableListOf<ChildData>()
    private lateinit var parentList: MutableList<ParentData>

    private lateinit var propertyList: MutableList<String>
    private lateinit var areaList: MutableList<String>
    private lateinit var vintageList: MutableList<String>


    private lateinit var entryAdapter: EntriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEntriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        areaList = mutableListOf("Selecione um talhão")
        propertyList = mutableListOf("Selecione uma propriedade")
        vintageList = mutableListOf("Selecione uma safra")

        getSpinners()

        binding.spinnerPropiety.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        binding.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        parentList = ArrayList()

        val parentData: MutableList<String> =
            mutableListOf(
                "Produto",
                "Sementes",
                "Capina, dessecação e pós emergência",
                "Fungicidas, Inseticidas e Foliares",
                "Operações",
                "Descritivo"
            )

//        val childDataData1: MutableList<ChildData> = mutableListOf(
//            ChildData("Anathapur"),
//            ChildData("Chittoor"),
//            ChildData("Nellore"),
//            ChildData("Guntur")
//        )
//        val childDataData2: MutableList<ChildData> = mutableListOf(
//            ChildData("Rajanna Sircilla"),
//            ChildData("Karimnagar"),
//            ChildData("Siddipet")
//        )
//        val childDataData3: MutableList<ChildData> =
//            mutableListOf(ChildData("Chennai"), ChildData("Erode"))

//        val parentObj1 = ParentData(parentTitle = parentData[0], subList = childDataData1)
//        val parentObj2 = ParentData(parentTitle = parentData[1], subList = childDataData2)
//        val parentObj3 = ParentData(parentTitle = parentData[2])
//        val parentObj4 = ParentData(parentTitle = parentData[1], subList = childDataData3)


        for (i in 0 until parentData.size) {
            val parentObj = ParentData(parentTitle = parentData[i])
            parentList.add(parentObj)
        }

        binding.rvEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntries.setHasFixedSize(true)
        entryAdapter = EntriesAdapter(parentList)

        val listener = object : EntryListener {
            override fun onListClick(selected: Boolean) {

            }
        }
        entryAdapter.attachListener(listener)

        //getAccomplishedEntries()
        initclicks()

        binding.rvEntries.adapter = entryAdapter

    }

    private fun getSpinners() {

        db.collection("Property").whereArrayContains("users", auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.get("name").toString()
                    propertyList.add(name)
                }
            }

        initAdapterSpinner(binding.spinnerPropiety,propertyList)
        initAdapterSpinner(binding.spinnerArea,areaList)
        initAdapterSpinner(binding.spinnerYear,vintageList)

    }

    private fun initAdapterSpinner(spinner: Spinner, list: MutableList<String>){
        val adapter =
            ArrayAdapter(
                requireContext(),
                androidx.transition.R.layout.support_simple_spinner_dropdown_item,
                list
            )
        spinner.adapter = adapter
    }


    private fun initclicks() {

        binding.buttonEditEntries.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_managerFragment)
        }
    }

    private fun getAccomplishedEntries() {
        db.collection("Entry").whereEqualTo("users", auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        entryAccomplishedList.clear()

                        for (document in documents) {
                            val description = document.get("description").toString()
                            val category = document.get("category").toString()
                            val quantity = document.getLong("quantity")
                            val unity = document.get("unity").toString()
                            val price = document.getLong("price")
                            val type = document.get("type").toString()
                            val total = document.getLong("total")

                            val newEntry = ChildData(
                                description,
                                category,
                                quantity,
                                unity,
                                price,
                                type,
                                total
                            )

                            //Log.d("db", "ID: ${document.id}  DADOS: ${document.data}")

                            entryAccomplishedList.add(newEntry)
                            //entryList.add(newEntry)
                            entryAdapter.updateAreas(parentList)
                        }

                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

