package com.example.tcc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.tcc.databinding.FragmentManagerBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ManagerFragment : Fragment() {

    private var _binding: FragmentManagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private  val db = FirebaseFirestore.getInstance()

    //private lateinit var entryAdapter: EntryManagerAdapter


    private lateinit var adapter : EntryManagePropertyAdapter

    private var dialog = AddDialogFragment("Propriedade")

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

        val listData: MutableList<ParentData> = ArrayList()

        val listener = object : EntryListener{

            override fun onListClick(selected: Boolean) {
                if(selected){
                    binding.buttonEdit.visibility = View.VISIBLE
                    binding.buttonEdit.visibility = View.VISIBLE
                } else {
                    binding.buttonEdit.visibility = View.INVISIBLE
                    binding.buttonEdit.visibility = View.INVISIBLE
                }
            }
        }
        adapter.attachListener(listener)

        val parentData: Array<String> =
            arrayOf("Produto",
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
        val parentObj4 = ParentData(parentTitle = parentData[1])

        listData.add(parentObj1)
        listData.add(parentObj2)
        listData.add(parentObj3)
        listData.add(parentObj4)

//        binding.rvEntriesManager.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvEntriesManager.setHasFixedSize(true)
//        entryAdapter = EntryManagerAdapter(requireContext(), listData)


        initClicks()

//        binding.rvEntriesManager.adapter = entryAdapter
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
            dialog.show(childFragmentManager, AddDialogFragment.TAG)
        }
    }


    private fun getEntries(){

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}