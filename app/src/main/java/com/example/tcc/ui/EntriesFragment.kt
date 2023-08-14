package com.example.tcc.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.model.ParentData
import com.example.tcc.R
import com.example.tcc.databinding.FragmentEntriesBinding
import com.example.tcc.model.ChildData
import com.example.tcc.ui.adapter.EntryManageEntriesAdapter
import com.example.tcc.ui.listeners.EntryListener

class EntriesFragment : Fragment() {

    private var _binding: FragmentEntriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var entryManageEntriesAdapter: EntryManageEntriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEntriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listData: MutableList<ParentData> = ArrayList()

        val parentData: MutableList<String> =
            mutableListOf("Produto",
                "Sementes",
                "Capina, dessecação e pós emergência",
                "Fungicidas, Inseticidas e Foliares",
                "Operações",
                "Descritivo")

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

        val parentObj1 = ParentData(parentTitle = parentData[0])
        val parentObj2 = ParentData(parentTitle = parentData[1])
        val parentObj3 = ParentData(parentTitle = parentData[2])
        val parentObj4 = ParentData(parentTitle = parentData[3])
        val parentObj5 = ParentData(parentTitle = parentData[4])
        val parentObj6 = ParentData(parentTitle = parentData[5])

        listData.add(parentObj1)
        listData.add(parentObj2)
        listData.add(parentObj3)
        listData.add(parentObj4)
        listData.add(parentObj5)
        listData.add(parentObj6)

        binding.rvEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntries.setHasFixedSize(true)
        entryManageEntriesAdapter = EntryManageEntriesAdapter(listData)

        val listener = object : EntryListener {
            override fun onListClick(selected: Boolean) {

            }
        }
        entryManageEntriesAdapter.attachListener(listener)

        initclicks()

        binding.rvEntries.adapter = entryManageEntriesAdapter

    }


    private fun initclicks() {

        binding.buttonEditEntries.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_managerFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    fun handleAddAreaDialog(){
//        val dialog = AddDialogFragment()
//        dialog.show(childFragmentManager, AddDialogFragment.TAG)
//    }

}

