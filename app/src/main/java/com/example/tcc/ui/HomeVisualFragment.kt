package com.example.tcc.ui

import android.os.Bundle
import android.util.Log
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.tcc.R
import com.example.tcc.databinding.FragmentEntriesBinding
import com.example.tcc.databinding.FragmentVisualHomeBinding
import com.example.tcc.model.DataBar
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeVisualFragment : Fragment() {

    private var _binding: FragmentVisualHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var propertyList: MutableList<String>
    private lateinit var areaList: MutableList<String>
    private lateinit var vintageList: MutableList<String>

    private var barSet : List<Pair<String,Int>> = mutableListOf()

    private val bartest = listOf("JAN" to 4.0,
        "FEB" to 7F,
        "MAR" to 2F,
        "MAY" to 2.3F,
        "APR" to 5F,
        "JUN" to 4F)

    private lateinit var barChart: BarChart

    private lateinit var barDataSet1: BarDataSet
    private lateinit var barDataSet2: BarDataSet

    var categories = arrayOf("Produto", "Semente", "CDE", "FIF", "Operações")

    companion object {
        private const val animationDuration = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisualHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        areaList = mutableListOf("Selecione um talhão")
        propertyList = mutableListOf("Selecione uma propriedade")
        vintageList = mutableListOf("Selecione uma safra")

        getSpinners()

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

        binding.spinnerPropiety.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                    binding.spinnerArea.setSelection(0)
                    binding.spinnerYear.setSelection(0)

                    /*areaList.clear()
                    vintageList.clear()*/

                    clearSpinnersList(areaList)
                    clearSpinnersList(vintageList)
                    binding.chartConstraint.visibility = View.GONE

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
                binding.chartConstraint.visibility = View.GONE

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

                    getLabels()




                } else {
                    binding.chartConstraint.visibility = View.GONE

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun createBarChart(arrayBudgeted : ArrayList<BarEntry>, arrayAccomplished : ArrayList<BarEntry>){

        barChart = binding.barChart

        barDataSet1 = BarDataSet(arrayBudgeted, "Orçado")
        barDataSet1.setColor(resources.getColor(R.color.purple_200))
        barDataSet2 = BarDataSet(arrayAccomplished, "Realizado")
        barDataSet2.setColor(resources.getColor(R.color.teal_200))

        var data = BarData(barDataSet1, barDataSet2)
        barChart.data = data
        barChart.description.isEnabled = false

        var xAxis = barChart.xAxis

        barDataSet1.valueTextSize = 8f
        barDataSet2.valueTextSize = 8f

        xAxis.valueFormatter = IndexAxisValueFormatter(categories)
        xAxis.setCenterAxisLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setGranularity(1f)
        xAxis.setGranularityEnabled(true)
        barChart.setDragEnabled(true)
        barChart.setVisibleXRangeMaximum(5f)
        val barSpace = 0.1f
        val groupSpace = 0.5f
        data.barWidth = 0.15f
        barChart.xAxis.axisMinimum = 0f
        barChart.animate()
        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.invalidate()
        binding.chartConstraint.visibility = View.VISIBLE
    }

    private fun getLabels(){
        val vintageRef = db.collection("Vintage")
        var totalProduct = 0f
        var totalSeeds = 0f
        var totalCDE = 0f
        var totalFIF = 0f
        var totalOperations = 0f

        val barBudgetedEntries : ArrayList<DataBar> = ArrayList()
        val barAccomplishedEntries : ArrayList<DataBar> = ArrayList()
        val barBudgetedList: ArrayList<BarEntry> = ArrayList()
        val barAccomplishedList: ArrayList<BarEntry> = ArrayList()

        vintageRef.whereArrayContains("users", auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("description") == binding.spinnerYear.selectedItem.toString()) {
                        db.collection("Entry").whereEqualTo("vintage", document.id)
                            .addSnapshotListener { snapshot, error ->
                                if (error == null) {
                                    val documents = snapshot?.documents
                                    if (documents != null) {
                                        for (documentEntry in documents) {
                                            val category = documentEntry.get("category").toString()
                                            val total = documentEntry.getDouble("total")
                                            val type = documentEntry.get("type").toString()

                                            val newEntry = DataBar(category,total)

                                            when(type){
                                                "Orçado" -> {
                                                    barBudgetedEntries.add(newEntry)
                                                }

                                                "Realizado" -> {
                                                    barAccomplishedEntries.add(newEntry)
                                                }
                                            }
                                        }

                                        barBudgetedEntries.forEach { item ->
                                            when(item.category){
                                                "Produto" -> {
                                                    totalProduct += item.total!!.toFloat()
                                                }
                                                "Sementes" -> {
                                                    totalSeeds += item.total!!.toFloat()
                                                }
                                                "Capina, dessecação e pós emergência" -> {
                                                    totalCDE += item.total!!.toFloat()
                                                }
                                                "Fungicidas, Inseticidas e Foliares" -> {
                                                    totalFIF += item.total!!.toFloat()
                                                }
                                                "Operações" -> {
                                                    totalOperations += item.total!!.toFloat()
                                                }
                                            }
                                        }

                                        barBudgetedList.add(BarEntry(1f, totalProduct))
                                        barBudgetedList.add(BarEntry(2f, totalSeeds))
                                        barBudgetedList.add(BarEntry(3f, totalCDE))
                                        barBudgetedList.add(BarEntry(4f, totalFIF))
                                        barBudgetedList.add(BarEntry(5f, totalOperations))

                                        totalProduct = 0f
                                        totalSeeds = 0f
                                        totalCDE = 0f
                                        totalFIF = 0f
                                        totalOperations = 0f

                                        barAccomplishedEntries.forEach { item ->
                                            when(item.category){
                                                "Produto" -> {
                                                    totalProduct += item.total!!.toFloat()
                                                }
                                                "Sementes" -> {
                                                    totalSeeds += item.total!!.toFloat()
                                                }
                                                "Capina, dessecação e pós emergência" -> {
                                                    totalCDE += item.total!!.toFloat()
                                                }
                                                "Fungicidas, Inseticidas e Foliares" -> {
                                                    totalFIF += item.total!!.toFloat()
                                                }
                                                "Operações" -> {
                                                    totalOperations += item.total!!.toFloat()
                                                }
                                            }
                                        }

                                        barAccomplishedList.add(BarEntry(1f, totalProduct))
                                        barAccomplishedList.add(BarEntry(2f, totalSeeds))
                                        barAccomplishedList.add(BarEntry(3f, totalCDE))
                                        barAccomplishedList.add(BarEntry(4f, totalFIF))
                                        barAccomplishedList.add(BarEntry(5f, totalOperations))

                                        createBarChart(barBudgetedList,barAccomplishedList)
                                    }
                                }


                            }
                    }
                }
            }
    }

    private fun initAdapterSpinner(spinner: Spinner, list: MutableList<String>) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
