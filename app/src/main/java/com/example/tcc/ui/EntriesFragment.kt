package com.example.tcc.ui

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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

    private lateinit var propertyList: MutableList<String>
    private lateinit var areaList: MutableList<String>
    private lateinit var vintageList: MutableList<String>

    private lateinit var parentList: MutableList<ParentData>

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
    private var descritiveExpanded = false

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

                    binding.spinnerArea.setSelection(0)
                    binding.spinnerYear.setSelection(0)

                    /*areaList.clear()
                    vintageList.clear()*/

                    clearSpinnersList(areaList)
                    clearSpinnersList(vintageList)
                    clearAllLists()
                    collapseAllRV()

                    if(position != 0){

                        val propRef = db.collection("Property")

                        propRef.whereArrayContains("users",auth.currentUser?.uid.toString())
                            .get().addOnSuccessListener { result ->
                                for(document in result){
                                    if(document.get("name") == binding.spinnerPropiety.selectedItem.toString()){
                                        db.collection("Area").whereEqualTo("property", document.id)
                                            .get().addOnSuccessListener { resultArea ->
                                                for(documentArea in resultArea){
                                                    areaList.add(documentArea.get("name").toString())
                                                }
                                            }
                                    }
                                }
                            }
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        binding.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                binding.spinnerYear.setSelection(0)

                clearSpinnersList(vintageList)
                clearAllLists()
                collapseAllRV()

                if(position != 0){

                    val areaRef = db.collection("Area")

                    areaRef.whereArrayContains("users",auth.currentUser?.uid.toString())
                        .get().addOnSuccessListener { result ->
                            for(document in result){
                                if(document.get("name") == binding.spinnerArea.selectedItem.toString()){
                                    db.collection("Vintage").whereEqualTo("area", document.id)
                                        .get().addOnSuccessListener { resultVintage ->
                                            for(documentArea in resultVintage){
                                                vintageList.add(documentArea.get("description").toString())
                                            }
                                        }
                                }
                            }
                        }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(position != 0){
                    if(binding.radioBudgeted.isChecked){
                        getEntries("Orçado", "Produto", productList, entryProductAdapter)
                        getEntries("Orçado", "Sementes", seedList, entrySeedAdapter)
                        getEntries("Orçado", "Capina, dessecação e pós emergência", cdeList, entryCDEAdapter)
                        getEntries("Orçado", "Fungicidas, Inseticidas e Foliares", fifList, entryFIFAdapter)
                        getEntries("Orçado", "Operações", operationsList, entryOperationsAdapter)
                        handleDescritive("Orçado")
                    } else {
                        getEntries("Realizado", "Produto", productList, entryProductAdapter)
                        getEntries("Realizado", "Sementes", seedList, entrySeedAdapter)
                        getEntries("Realizado", "Capina, dessecação e pós emergência", cdeList, entryCDEAdapter)
                        getEntries("Realizado", "Fungicidas, Inseticidas e Foliares", fifList, entryFIFAdapter)
                        getEntries("Realizado", "Operações", operationsList, entryOperationsAdapter)
                        handleDescritive("Realizado")
                    }
                } else {
                    clearAllLists()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        initAdapters()

        binding.radioGroupEntries.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_budgeted -> {

                    if(binding.spinnerYear.selectedItem != 0){
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
                        handleDescritive("Orçado")

                        binding.rvEntriesProduct.visibility = View.GONE
                        binding.rvEntriesSeed.visibility = View.GONE
                        binding.rvEntriesCDE.visibility = View.GONE
                        binding.rvEntriesFIF.visibility = View.GONE
                        binding.rvEntriesOperations.visibility = View.GONE
                        binding.constraintDescritive.visibility = View.GONE

                        productExpanded = false
                        seedExpanded = false
                        cdeExpanded = false
                        fifExpanded = false
                        operationsExpanded = false
                        descritiveExpanded = false
                    }
                }

                R.id.radio_accomplished -> {

                    if(binding.spinnerYear.selectedItem != 0){
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
                        handleDescritive("Realizado")

                        binding.rvEntriesProduct.visibility = View.GONE
                        binding.rvEntriesSeed.visibility = View.GONE
                        binding.rvEntriesCDE.visibility = View.GONE
                        binding.rvEntriesFIF.visibility = View.GONE
                        binding.rvEntriesOperations.visibility = View.GONE
                        binding.constraintDescritive.visibility = View.GONE

                        productExpanded = false
                        seedExpanded = false
                        cdeExpanded = false
                        fifExpanded = false
                        operationsExpanded = false
                        descritiveExpanded = false
                    }
                }
            }
        }

        initclicks()

    }

    private fun collapseAllRV(){
        binding.downIvProduct.setImageResource(R.drawable.ic_drop_down)
        binding.rvEntriesProduct.visibility = View.GONE
        productExpanded = false

        binding.downIvSeed.setImageResource(R.drawable.ic_drop_down)
        binding.rvEntriesSeed.visibility = View.GONE
        seedExpanded = false

        binding.downIvCDE.setImageResource(R.drawable.ic_drop_down)
        binding.rvEntriesCDE.visibility = View.GONE
        cdeExpanded = false

        binding.downIvFIF.setImageResource(R.drawable.ic_drop_down)
        binding.rvEntriesFIF.visibility = View.GONE
        fifExpanded = false

        binding.downIvOperations.setImageResource(R.drawable.ic_drop_down)
        binding.rvEntriesOperations.visibility = View.GONE
        operationsExpanded = false

        binding.downIvDescritive.setImageResource(R.drawable.ic_drop_down)
        binding.constraintDescritive.visibility = View.GONE
        descritiveExpanded = false
    }

    private fun getEntries(typeEntry: String, categoryEntry: String, list: MutableList<ChildData>, adapter: EntryManageEntriesAdapter) {

        val vintageRef = db.collection("Vintage")

        vintageRef.whereArrayContains("users", auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("description") == binding.spinnerYear.selectedItem.toString()) {
                        db.collection("Entry").whereEqualTo("vintage", document.id)
                            .addSnapshotListener { snapshot, error ->
                                if(error == null){
                                    val documents = snapshot?.documents
                                    if (documents != null) {
                                        list.clear()
                                        for(documentEntry in  documents) {
                                            val description = documentEntry.get("description").toString()
                                            val category = documentEntry.get("category").toString()
                                            val quantity = documentEntry.getDouble("quantity")
                                            val unity = documentEntry.get("unity").toString()
                                            val price = documentEntry.getDouble("price")
                                            val type = documentEntry.get("type").toString()
                                            val total = documentEntry.getDouble("total")

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
                }
            }
    }


    private fun initAdapters() {
        val listenerProduct = object : EntryListener {
            override fun onListClick(selected: Boolean) {}
        }

        val listenerSeed = object : EntryListener {
            override fun onListClick(selected: Boolean) {}
        }

        val listenerCDE = object : EntryListener {
            override fun onListClick(selected: Boolean) {}
        }

        val listenerFIF = object : EntryListener {
            override fun onListClick(selected: Boolean) {}
        }

        val listenerOperations = object : EntryListener {
            override fun onListClick(selected: Boolean) {}
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

    private fun handleDescritive(typeDescritive: String){
        var totalProduct = 0f
        var totalSeed = 0f
        var totalCDE = 0f
        var totalFIF = 0f
        var totalOperations = 0f
        var totalAbsolute: Float

        val df = DecimalFormat("#,##")

        val vintageRef = db.collection("Vintage")

        vintageRef.whereArrayContains("users", auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("description") == binding.spinnerYear.selectedItem.toString()) {
                        db.collection("Entry").whereEqualTo("vintage", document.id)
                            .addSnapshotListener { snapshot, error ->
                                if(error == null){
                                    val documents = snapshot?.documents
                                    if (documents != null) {
                                        for(documentEntry in  documents) {
                                            val category = documentEntry.get("category").toString()
                                            val total = documentEntry.getDouble("total")
                                            val type = documentEntry.get("type").toString()

                                            if(type == typeDescritive){
                                                when(category){
                                                    "Produto" -> {
                                                        totalProduct += total!!.toFloat()
                                                    }
                                                    "Sementes" -> {
                                                        totalSeed += total!!.toFloat()
                                                    }
                                                    "Capina, dessecação e pós emergência" -> {
                                                        totalCDE += total!!.toFloat()
                                                    }
                                                    "Fungicidas, Inseticidas e Foliares" -> {
                                                        totalFIF += total!!.toFloat()
                                                    }
                                                    "Operações" -> {
                                                        totalOperations += total!!.toFloat()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                totalAbsolute = totalProduct + totalSeed + totalCDE + totalFIF + totalOperations
                                df.format(totalAbsolute)


                                    //totalOperations.toString().replace(".", ",")

                                binding.textOperationsDescritive.text = "R$ ${String.format("%.2f", totalOperations).replace(".", ",")}"
                                binding.textCorrectivesFertilizers.text = "R$ ${String.format("%.2f", totalProduct).replace(".", ",")}"
                                binding.textSeeds.text = "R$ ${String.format("%.2f", totalSeed).replace(".", ",")}"
                                binding.textDefensives.text = "R$ ${String.format("%.2f", totalCDE + totalFIF).replace(".", ",")}"
                                binding.textTotal.text = "R$ ${String.format("%.2f", totalAbsolute).replace(".", ",")}"

                                if(totalOperations == 0f){
                                    binding.textPercentageOperations.text = "0 %"
                                } else {
                                    binding.textPercentageOperations.text = "${df.format((totalOperations/totalAbsolute)*100)} %"
                                }

                                if(totalSeed == 0f){
                                    binding.textPercentageSeeds.text = "0 %"
                                } else {
                                    binding.textPercentageSeeds.text = "${(df.format((totalSeed / totalAbsolute )*100))} %"
                                }

                                if((totalCDE + totalFIF) == 0f){
                                    binding.textPercentageDefensives.text = "0 %"
                                } else {
                                    binding.textPercentageDefensives.text = "${df.format((((totalCDE + totalFIF) / totalAbsolute)*100))} %"
                                }

                                if(totalProduct == 0f){
                                    binding.textPercentageCorretivesFertilizers.text = "0 %"
                                } else {
                                    binding.textPercentageCorretivesFertilizers.text = "${df.format(((totalProduct / totalAbsolute)*100))} %"
                                }
                            }
                    }
                }
            }
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

    private fun clearSpinnersList(list: MutableList<String>){
        if(list.size > 1){
            for(i in 1 until list.size){
                list.removeAt(i)
            }
        }
    }

    private fun clearAllLists(){
        productList.clear()
        seedList.clear()
        cdeList.clear()
        fifList.clear()
        operationsList.clear()
    }


    private fun initclicks() {

        binding.buttonEditEntries.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_managerFragment)
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

        binding.textOperations.setOnClickListener {
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

        binding.textDescritive.setOnClickListener {
            if (descritiveExpanded) {
                binding.downIvDescritive.setImageResource(R.drawable.ic_drop_down)
                binding.constraintDescritive.visibility = View.GONE
                descritiveExpanded = false
            } else {
                binding.downIvDescritive.setImageResource(R.drawable.ic_drop_up)
                binding.constraintDescritive.visibility = View.VISIBLE
                descritiveExpanded = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

