package com.example.tcc.ui

import android.os.Bundle
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

    private lateinit var barEntriesList: ArrayList<BarEntry>

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

        //binding.barChart.animation.duration = animationDuration


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
                    /*getLabels("Produto")
                    getLabels("Sementes")
                    getLabels("Capina, dessecação e pós emergência")
                    getLabels("Fungicidas, Inseticidas e Foliares")
                    getLabels("Operações")*/

                    //binding.barChart.animate(bartest)

                    createBarChart()

                    binding.chartConstraint.visibility = View.VISIBLE


                } else {
                    binding.chartConstraint.visibility = View.GONE

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun createBarChart(){

        barChart = binding.barChart

        barDataSet1 = BarDataSet(getBarChartDataForSet1(), "Orçado")
        barDataSet1.setColor(resources.getColor(R.color.purple_200))
        barDataSet2 = BarDataSet(getBarChartDataForSet2(), "Realizado")
        barDataSet2.setColor(resources.getColor(R.color.teal_200))

        var data = BarData(barDataSet1, barDataSet2)
        barChart.data = data
        barChart.description.isEnabled = false

        var xAxis = barChart.xAxis
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
    }

    private fun getLabels(categoryEntry: String){
        val vintageRef = db.collection("Vintage")
        var totalLabelBudgeted = 0.0
        var totalLabelAccomplished = 0.0


        vintageRef.whereArrayContains("users", auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.get("description") == binding.spinnerYear.selectedItem.toString()) {
                        db.collection("Entry").whereEqualTo("vintage", document.id)
                            .addSnapshotListener { snapshot, error ->
                                if (error == null) {
                                    val documents = snapshot?.documents
                                    if (documents != null) {

                                        totalLabelBudgeted = 0.0
                                        totalLabelAccomplished = 0.0

                                        for (documentEntry in documents) {
                                            val category = documentEntry.get("category").toString()
                                            val total = documentEntry.getDouble("total")
                                            val type = documentEntry.get("type").toString()

                                            when(type){
                                                "Orçado" -> {
                                                    if(categoryEntry == category){
                                                        totalLabelBudgeted += total!!
                                                    }
                                                }

                                                "Realizado" -> {
                                                    if(categoryEntry == category){
                                                        totalLabelAccomplished += total!!
                                                    }
                                                }
                                            }

                                        }


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

    private fun getBarChartDataForSet1(): ArrayList<BarEntry> {
        barEntriesList = ArrayList()
        // on below line we are adding
        // data to our bar entries list
        barEntriesList.add(BarEntry(1f, 1f))
        barEntriesList.add(BarEntry(2f, 2f))
        barEntriesList.add(BarEntry(3f, 3f))
        barEntriesList.add(BarEntry(4f, 4f))
        barEntriesList.add(BarEntry(5f, 5f))
        return barEntriesList
    }

    private fun getBarChartDataForSet2(): ArrayList<BarEntry> {
        barEntriesList = ArrayList()
        // on below line we are adding data
        // to our bar entries list
        barEntriesList.add(BarEntry(1f, 2f))
        barEntriesList.add(BarEntry(2f, 4f))
        barEntriesList.add(BarEntry(3f, 6f))
        barEntriesList.add(BarEntry(4f, 8f))
        barEntriesList.add(BarEntry(5f, 10f))
        return barEntriesList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
