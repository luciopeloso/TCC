package com.example.tcc.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
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
import com.example.tcc.ui.adapter.EntriesAdapter
import com.example.tcc.ui.adapter.EntryManageEntriesAdapter
import com.example.tcc.ui.adapter.EntryManageVintageAdapter
import com.example.tcc.ui.listeners.EntryListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EntriesManagerFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private val args: EntriesManagerFragmentArgs? by navArgs()

    private var _binding: FragmentEntriesManagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var entryManageEntriesAdapter: EntryManageEntriesAdapter
    private val entryList = mutableListOf<ChildData>()
    private val entryBudgetedList = mutableListOf<ChildData>()
    private val entryAccomplishedList = mutableListOf<ChildData>()
    private lateinit var parentList: MutableList<ParentData>

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var dialogAdd: AddEntriesDialogFragment

    private val entry = ChildData(
        null, null, null,
        null, null, null, null
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntriesManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        parentList = ArrayList()

        val parentData: MutableList<String> =
            mutableListOf(
                "Produto",
                "Sementes",
                "Capina, dessecação e pós emergência",
                "Fungicidas, Inseticidas e Foliares",
                "Operações"
            )

        for (i in 0 until parentData.size) {
            val parentObj = ParentData(parentTitle = parentData[i])
            parentList.add(parentObj)
        }

        /*val childData: MutableList<ChildData> = mutableListOf(
            ChildData("Sementes","semente A", 3,"unidades", 2, 0, 6),
            ChildData("Sementes","semente B", 2,"unidades", 3, 0, 6))
        val parentObj = ParentData(parentTitle = parentData[0], subList = childData)
        parentList.add(parentObj)*/

        Log.d("db", "ID Safra: ${args?.vintageId}")



        val listener = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                if (selected) {
                    entry.description = entryList[entryManageEntriesAdapter.positionSelected].description
                    entry.quantity = entryList[entryManageEntriesAdapter.positionSelected].quantity
                    entry.unity = entryList[entryManageEntriesAdapter.positionSelected].unity
                    entry.category = entryList[entryManageEntriesAdapter.positionSelected].category
                    entry.price = entryList[entryManageEntriesAdapter.positionSelected].price
                    entry.type = entryList[entryManageEntriesAdapter.positionSelected].type
                    entry.total = entryList[entryManageEntriesAdapter.positionSelected].total
                    binding.buttonEdit.visibility = View.VISIBLE

                } else {
                    binding.buttonEdit.visibility = View.INVISIBLE

                }
            }
        }

        binding.rvEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntries.setHasFixedSize(true)
        entryManageEntriesAdapter = EntryManageEntriesAdapter(parentList)
        binding.rvEntries.adapter = entryManageEntriesAdapter

        entryManageEntriesAdapter.attachListener(listener)

        //getEntries()

        getBudgetedEntries()
        getAccomplishedEntries()

        binding.radioBudgeted.setOnCheckedChangeListener(this)

        initClicks()
    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_entriesManagerFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener {
            navigate(EntriesManagerFragmentDirections
                .actionEntriesManagerFragmentToVintageManagerFragment(args?.areaId,args?.propId))
        }

        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddEntriesDialogFragment(null, args?.vintageId)
            dialogAdd.show(childFragmentManager, AddVintageDialogFragment.TAG)
        }

        binding.buttonEdit.setOnClickListener {

            if(binding.radioBudgeted.isSelected){
                dialogAdd =
                    AddEntriesDialogFragment(entryBudgetedList[entryManageEntriesAdapter.positionSelected], args?.vintageId)
                dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)
            } else {
                dialogAdd =
                    AddEntriesDialogFragment(entryAccomplishedList[entryManageEntriesAdapter.positionSelected], args?.vintageId)
                dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)
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
    private fun getAccomplishedEntries() {
        db.collection("Entry").whereEqualTo("vintage", args?.vintageId)
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
                            val type = document.getLong("type")
                            val total = document.getLong("total")

                            val newEntry = ChildData(
                                description,
                                category,
                                quantity ,
                                unity,
                                price ,
                                type ,
                                total
                            )

                            //Log.d("db", "ID: ${document.id}  DADOS: ${document.data}")

                            entryAccomplishedList.add(newEntry)
                            entryList.add(newEntry)

                            parentList.forEach { item ->
                                if(item.parentTitle == category){
                                    item.subList?.add(newEntry)
                                }
                            }

                            entryManageEntriesAdapter.updateEntries(parentList)
                        }

                    }
                }
            }
    }

    private fun getBudgetedEntries() {
        db.collection("Entry").whereEqualTo("vintage", args?.vintageId)
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        entryBudgetedList.clear()

                        for (document in documents) {
                            val description = document.get("description").toString()
                            val category = document.get("category").toString()
                            val quantity = document.getLong("quantity")
                            val unity = document.get("unity").toString()
                            val price = document.getLong("price")
                            val type = document.getLong("type")
                            val total = document.getLong("total")

                            val newEntry = ChildData(
                                description,
                                category,
                                quantity ,
                                unity,
                                price ,
                                type ,
                                total
                            )

                            //Log.d("db", "ID: ${document.id}  DADOS: ${document.data}")

                            entryBudgetedList.add(newEntry)
                            entryList.add(newEntry)
                            entryManageEntriesAdapter.updateEntries(parentList)
                        }

                    }
                }
            }
    }

    override fun onCheckedChanged(button: CompoundButton, isChecked: Boolean) {
        when (button.id) {
            R.id.radio_budgeted -> {
                if(parentList.size != 0){
                    for (i in 0 until parentList.size) {
                        entryManageEntriesAdapter.collapseParentRow(i)
                    }
                }
            }

            R.id.radio_accomplished -> {
                if(parentList.size != 0){
                    for (i in 0 until parentList.size) {
                        entryManageEntriesAdapter.collapseParentRow(i)
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