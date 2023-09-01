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
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.R
import com.example.tcc.databinding.FragmentEntriesManagerBinding
import com.example.tcc.dialogs.AddAreaDialogFragment
import com.example.tcc.dialogs.AddEntriesDialogFragment
import com.example.tcc.dialogs.AddVintageDialogFragment
import com.example.tcc.model.ChildData
import com.example.tcc.model.ParentData
import com.example.tcc.ui.adapter.EntryManageEntriesAdapter
import com.example.tcc.ui.listeners.EntryListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EntriesManagerFragment : Fragment() {

    //, CompoundButton.OnCheckedChangeListener

    private val args: EntriesManagerFragmentArgs? by navArgs()

    private var _binding: FragmentEntriesManagerBinding? = null
    private val binding get() = _binding!!

    private lateinit var entryManageEntriesAdapter: EntryManageEntriesAdapter

    private lateinit var entryProductAdapter: EntryManageEntriesAdapter
    private lateinit var entrySeedAdapter: EntryManageEntriesAdapter
    private lateinit var entryCDEAdapter: EntryManageEntriesAdapter
    private lateinit var entryFIFAdapter: EntryManageEntriesAdapter
    private lateinit var entryOperationsAdapter: EntryManageEntriesAdapter

    private val productList = mutableListOf<ChildData>()
    private val seedList = mutableListOf<ChildData>()
    private val cdeList = mutableListOf<ChildData>()
    private val fifList = mutableListOf<ChildData>()
    private val operationsList = mutableListOf<ChildData>()

    private var productExpanded = false
    private var seedExpanded = false
    private var cdeExpanded = false
    private var fifExpanded = false
    private var operationsExpanded = false

    private var anotherSelected = 0


    private var typeClick: String = ""


    private lateinit var parentList: MutableList<String>

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

        Log.d("db", "ID Safra: ${args?.vintageId}")


        initAdapters()


        getEntries("Orçado", "Produto", productList, entryProductAdapter)
        getEntries("Orçado", "Sementes", seedList, entrySeedAdapter)
        getEntries("Orçado", "Capina, dessecação e pós emergência", cdeList, entryCDEAdapter)
        getEntries("Orçado", "Fungicidas, Inseticidas e Foliares", fifList, entryFIFAdapter)
        getEntries("Orçado", "Operações", operationsList, entryOperationsAdapter)

        binding.radioGroupEntries.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_budgeted -> {
                    entryProductAdapter.positionSelected = RecyclerView.NO_POSITION
                    entrySeedAdapter.positionSelected = RecyclerView.NO_POSITION
                    entryCDEAdapter.positionSelected = RecyclerView.NO_POSITION
                    entryFIFAdapter.positionSelected = RecyclerView.NO_POSITION
                    entryOperationsAdapter.positionSelected = RecyclerView.NO_POSITION
                    getEntries("Orçado", "Produto", productList, entryProductAdapter)
                    getEntries("Orçado", "Sementes", seedList, entrySeedAdapter)
                    getEntries("Orçado","Capina, dessecação e pós emergência", cdeList, entryCDEAdapter)
                    getEntries(
                        "Orçado",
                        "Fungicidas, Inseticidas e Foliares",
                        fifList,
                        entryFIFAdapter
                    )
                    getEntries("Orçado", "Operações", operationsList, entryOperationsAdapter)


                    binding.rvEntriesProduct.visibility = View.GONE
                    binding.rvEntriesSeed.visibility = View.GONE
                    binding.rvEntriesCDE.visibility = View.GONE
                    binding.rvEntriesFIF.visibility = View.GONE
                    binding.rvEntriesOperations.visibility = View.GONE

                    productExpanded = false
                    seedExpanded = false
                    cdeExpanded = false
                    fifExpanded = false
                    operationsExpanded = false
                }

                R.id.radio_accomplished -> {
                    entryProductAdapter.positionSelected = RecyclerView.NO_POSITION
                    entrySeedAdapter.positionSelected = RecyclerView.NO_POSITION
                    entryCDEAdapter.positionSelected = RecyclerView.NO_POSITION
                    entryFIFAdapter.positionSelected = RecyclerView.NO_POSITION
                    entryOperationsAdapter.positionSelected = RecyclerView.NO_POSITION
                    getEntries("Realizado", "Produto", productList, entryProductAdapter)
                    getEntries("Realizado", "Sementes", seedList, entrySeedAdapter)
                    getEntries(
                        "Realizado",
                        "Capina, dessecação e pós emergência",
                        cdeList,
                        entryCDEAdapter
                    )
                    getEntries(
                        "Realizado",
                        "Fungicidas, Inseticidas e Foliares",
                        fifList,
                        entryFIFAdapter
                    )
                    getEntries("Realizado", "Operações", operationsList, entryOperationsAdapter)

                    binding.rvEntriesProduct.visibility = View.GONE
                    binding.rvEntriesSeed.visibility = View.GONE
                    binding.rvEntriesCDE.visibility = View.GONE
                    binding.rvEntriesFIF.visibility = View.GONE
                    binding.rvEntriesOperations.visibility = View.GONE

                    productExpanded = false
                    seedExpanded = false
                    cdeExpanded = false
                    fifExpanded = false
                    operationsExpanded = false
                }
            }
        }

        getVintageName()

        initClicks()
    }

    private fun initClicks() {
        binding.ibLogout.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_entriesManagerFragment_to_loginFragment)
        }
        binding.ibBack.setOnClickListener {
            navigate(
                EntriesManagerFragmentDirections
                    .actionEntriesManagerFragmentToVintageManagerFragment(
                        args?.areaId,
                        args?.propId
                    )
            )
        }

        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddEntriesDialogFragment(null, args?.vintageId)
            dialogAdd.show(childFragmentManager, AddVintageDialogFragment.TAG)
        }

        binding.textProduct.setOnClickListener {
            if (productExpanded) {
                binding.downIvProduct.setImageResource(R.drawable.ic_drop_down)
                binding.rvEntriesProduct.visibility = View.GONE
                productExpanded = false

            } else {
                binding.downIvProduct.setImageResource(R.drawable.ic_drop_up)
                binding.rvEntriesProduct.visibility = View.VISIBLE
                productExpanded = true
            }
        }

        binding.textSeed.setOnClickListener {
            if (seedExpanded) {
                binding.downIvSeed.setImageResource(R.drawable.ic_drop_down)
                binding.rvEntriesSeed.visibility = View.GONE
                seedExpanded = false
            } else {
                binding.downIvSeed.setImageResource(R.drawable.ic_drop_up)
                binding.rvEntriesSeed.visibility = View.VISIBLE
                seedExpanded = true
            }
        }

        binding.textCDE.setOnClickListener {
            if (cdeExpanded) {
                binding.downIvCDE.setImageResource(R.drawable.ic_drop_down)
                binding.rvEntriesCDE.visibility = View.GONE
                cdeExpanded = false
            } else {
                binding.downIvCDE.setImageResource(R.drawable.ic_drop_up)
                binding.rvEntriesCDE.visibility = View.VISIBLE
                cdeExpanded = true
            }
        }

        binding.textFIF.setOnClickListener {
            if (fifExpanded) {
                binding.downIvFIF.setImageResource(R.drawable.ic_drop_down)
                binding.rvEntriesFIF.visibility = View.GONE
                fifExpanded = false
            } else {
                binding.downIvFIF.setImageResource(R.drawable.ic_drop_up)
                binding.rvEntriesFIF.visibility = View.VISIBLE
                fifExpanded = true
            }
        }

        binding.textOpeartions.setOnClickListener {
            if (operationsExpanded) {
                binding.downIvOperations.setImageResource(R.drawable.ic_drop_down)
                binding.rvEntriesOperations.visibility = View.GONE
                operationsExpanded = false
            } else {
                binding.downIvOperations.setImageResource(R.drawable.ic_drop_up)
                binding.rvEntriesOperations.visibility = View.VISIBLE
                operationsExpanded = true
            }
        }
    }

    private fun getVintageName(){
        db.collection("Property").document(args?.propId!!)
            .get().addOnSuccessListener { documentProp ->
                val nameProp = documentProp.get("name").toString()
                db.collection("Area").document(args?.areaId!!)
                    .get().addOnSuccessListener { documentArea ->
                    val nameArea = documentArea.get("name").toString()
                        db.collection("Vintage").document(args?.vintageId!!)
                            .get().addOnSuccessListener { documentVintage ->
                                val nameVintage = documentVintage.get("description").toString()
                                binding.textNavigation.text = "Propriedade: $nameProp \nTalhão: $nameArea\n Safra: $nameVintage"
                            }
                    }
            }
    }

    private fun initAdapters() {

        val listenerProduct = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                entry.description =
                    productList[entryProductAdapter.positionSelected].description
                entry.quantity = productList[entryProductAdapter.positionSelected].quantity
                entry.unity = productList[entryProductAdapter.positionSelected].unity
                entry.category = productList[entryProductAdapter.positionSelected].category
                entry.price = productList[entryProductAdapter.positionSelected].price
                entry.type = productList[entryProductAdapter.positionSelected].type
                entry.total = productList[entryProductAdapter.positionSelected].total
                //binding.buttonEdit.visibility = View.VISIBLE

                dialogAdd =
                    AddEntriesDialogFragment(
                        productList[entryProductAdapter.positionSelected],
                        args?.vintageId
                    )
                dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)

                typeClick = entry.category!!
            }
        }

        val listenerSeed = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                entry.description = seedList[entrySeedAdapter.positionSelected].description
                entry.quantity = seedList[entrySeedAdapter.positionSelected].quantity
                entry.unity = seedList[entrySeedAdapter.positionSelected].unity
                entry.category = seedList[entrySeedAdapter.positionSelected].category
                entry.price = seedList[entrySeedAdapter.positionSelected].price
                entry.type = seedList[entrySeedAdapter.positionSelected].type
                entry.total = seedList[entrySeedAdapter.positionSelected].total
                //binding.buttonEdit.visibility = View.VISIBLE

                dialogAdd =
                    AddEntriesDialogFragment(
                        seedList[entrySeedAdapter.positionSelected],
                        args?.vintageId
                    )
                dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)

                typeClick = entry.category!!
            }
        }

        val listenerCDE = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                entry.description = cdeList[entryCDEAdapter.positionSelected].description
                entry.quantity = cdeList[entryCDEAdapter.positionSelected].quantity
                entry.unity = cdeList[entryCDEAdapter.positionSelected].unity
                entry.category = cdeList[entryCDEAdapter.positionSelected].category
                entry.price = cdeList[entryCDEAdapter.positionSelected].price
                entry.type = cdeList[entryCDEAdapter.positionSelected].type
                entry.total = cdeList[entryCDEAdapter.positionSelected].total
                //binding.buttonEdit.visibility = View.VISIBLE

                dialogAdd =
                    AddEntriesDialogFragment(
                        cdeList[entryCDEAdapter.positionSelected],
                        args?.vintageId
                    )
                dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)

                typeClick = entry.category!!
            }
        }

        val listenerFIF = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                entry.description = fifList[entryFIFAdapter.positionSelected].description
                entry.quantity = fifList[entryFIFAdapter.positionSelected].quantity
                entry.unity = fifList[entryFIFAdapter.positionSelected].unity
                entry.category = fifList[entryFIFAdapter.positionSelected].category
                entry.price = fifList[entryFIFAdapter.positionSelected].price
                entry.type = fifList[entryFIFAdapter.positionSelected].type
                entry.total = fifList[entryFIFAdapter.positionSelected].total
                //binding.buttonEdit.visibility = View.VISIBLE

                dialogAdd =
                    AddEntriesDialogFragment(
                        fifList[entryFIFAdapter.positionSelected],
                        args?.vintageId
                    )
                dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)

                typeClick = entry.category!!
            }
        }

        val listenerOperations = object : EntryListener {
            override fun onListClick(selected: Boolean) {
                entry.description =
                    operationsList[entryOperationsAdapter.positionSelected].description
                entry.quantity =
                    operationsList[entryOperationsAdapter.positionSelected].quantity
                entry.unity = operationsList[entryOperationsAdapter.positionSelected].unity
                entry.category =
                    operationsList[entryOperationsAdapter.positionSelected].category
                entry.price = operationsList[entryOperationsAdapter.positionSelected].price
                entry.type = operationsList[entryOperationsAdapter.positionSelected].type
                entry.total = operationsList[entryOperationsAdapter.positionSelected].total
                //binding.buttonEdit.visibility = View.VISIBLE

                dialogAdd =
                    AddEntriesDialogFragment(
                        operationsList[entryOperationsAdapter.positionSelected],
                        args?.vintageId
                    )
                dialogAdd.show(childFragmentManager, AddAreaDialogFragment.TAG)

                typeClick = entry.category!!

            }
        }

        binding.rvEntriesProduct.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntriesProduct.setHasFixedSize(true)
        entryProductAdapter = EntryManageEntriesAdapter(productList)
        binding.rvEntriesProduct.adapter = entryProductAdapter
        entryProductAdapter.attachListener(listenerProduct)


        binding.rvEntriesSeed.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntriesSeed.setHasFixedSize(true)
        entrySeedAdapter = EntryManageEntriesAdapter(seedList)
        binding.rvEntriesSeed.adapter = entrySeedAdapter
        entrySeedAdapter.attachListener(listenerSeed)

        binding.rvEntriesCDE.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntriesCDE.setHasFixedSize(true)
        entryCDEAdapter = EntryManageEntriesAdapter(cdeList)
        binding.rvEntriesCDE.adapter = entryCDEAdapter
        entryCDEAdapter.attachListener(listenerCDE)

        binding.rvEntriesFIF.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntriesFIF.setHasFixedSize(true)
        entryFIFAdapter = EntryManageEntriesAdapter(fifList)
        binding.rvEntriesFIF.adapter = entryFIFAdapter
        entryFIFAdapter.attachListener(listenerFIF)

        binding.rvEntriesOperations.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntriesOperations.setHasFixedSize(true)
        entryOperationsAdapter = EntryManageEntriesAdapter(operationsList)
        binding.rvEntriesOperations.adapter = entryOperationsAdapter
        entryOperationsAdapter.attachListener(listenerOperations)

    }


    private fun Fragment.navigate(directions: NavDirections) {
        val controller = findNavController()
        val currentDestination =
            (controller.currentDestination as? FragmentNavigator.Destination)?.className
                ?: (controller.currentDestination as? DialogFragmentNavigator.Destination)?.className
        if (currentDestination == this.javaClass.name) {
            controller.navigate(directions)
        }
    }

    private fun getEntries(
        typeEntry: String,
        categoryEntry: String,
        list: MutableList<ChildData>,
        adapter: EntryManageEntriesAdapter
    ) {
        db.collection("Entry").whereEqualTo("vintage", args?.vintageId)
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        list.clear()

                        for (document in documents) {
                            val description = document.get("description").toString()
                            val category = document.get("category").toString()
                            val quantity = document.getLong("quantity")
                            val unity = document.get("unity").toString()
                            val price = document.getLong("price")
                            val type = document.get("type").toString()
                            val total = document.getLong("total")

                            val newEntry = ChildData(
                                category,
                                description,
                                quantity,
                                unity,
                                price,
                                type,
                                total
                            )

                            if (type == typeEntry && categoryEntry == category) {
                                list.add(newEntry)
                                adapter.updateEntries(list)
                            }
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