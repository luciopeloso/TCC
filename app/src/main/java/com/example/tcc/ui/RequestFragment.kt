package com.example.tcc.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.R
import com.example.tcc.databinding.FragmentHomeBinding
import com.example.tcc.databinding.FragmentRequestBinding
import com.example.tcc.dialogs.AddPropertyDialogFragment
import com.example.tcc.dialogs.AddRequestDialogFragment
import com.example.tcc.model.Customer
import com.example.tcc.model.Property
import com.example.tcc.model.Request
import com.example.tcc.ui.adapter.EntryManagePropertyAdapter
import com.example.tcc.ui.adapter.RequestManageReceivedAdapter
import com.example.tcc.ui.adapter.RequestManageSendedAdapter
import com.example.tcc.ui.adapter.RequestReceivedAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var  requestManageReceivedAdapter : RequestManageReceivedAdapter
    private lateinit var  requestReceivedAdapter : RequestReceivedAdapter
    private lateinit var  requestManageSendedAdapter : RequestManageSendedAdapter

    private val requestManageReceivedList = mutableListOf<Request>()
    private val requestManageSendedList = mutableListOf<Request>()
    private val requestReceivedList = mutableListOf<Request>()

    private lateinit var dialogAdd: AddRequestDialogFragment

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        getEntries()

        initClicks()

    }

    private fun initAdapter(senderCustomer: Customer,receiverCustomer: Customer){


        binding.rvAccessSended.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAccessSended.setHasFixedSize(true)
        requestManageSendedAdapter = RequestManageSendedAdapter(requestManageSendedList,senderCustomer)
        binding.rvAccessSended.adapter = requestManageSendedAdapter
    }

    private fun optionSelect(request: Request, select: Int) {

    }

    private fun initClicks() {
        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddRequestDialogFragment()
            dialogAdd.show(childFragmentManager, AddRequestDialogFragment.TAG)
        }
    }

    private fun getEntries() {
        db.collection("Request").whereEqualTo("Receiver",auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        requestReceivedList.clear()

                        for (document in documents) {

                            val status = document.get("status").toString()
                            val accept = document.getBoolean("accept")
                            val senderExclude = document.getBoolean("senderExlude")
                            val receiverExclude = document.getBoolean("receiverExclude")
                            val sender = document.get("sender").toString()
                            val receiver = document.get("receiver").toString()

                            val newRequest = Request(status, sender, receiver, accept as Boolean,
                                senderExclude as Boolean, receiverExclude as Boolean)

                            if(document.get("status").toString() == "Enviado"){
                                /*db.collection("Customer").document(sender).get().addOnSuccessListener { result ->
                                    val senderObject = Customer(null,null,null,null)
                                    senderObject.name = result.get("name").toString()
                                    senderObject.lastName = result.get("lastName").toString()
                                    senderObject.acessType = result.get("acessType").toString()
                                    senderObject.email = result.get("email").toString()
                                }

                                db.collection("Customer").document(receiver).get().addOnSuccessListener { result ->
                                    val receiverObject = Customer(null,null,null,null)
                                    receiverObject.name = result.get("name").toString()
                                    receiverObject.lastName = result.get("lastName").toString()
                                    receiverObject.acessType = result.get("acessType").toString()
                                    receiverObject.email = result.get("email").toString()
                                }*/


                                requestReceivedList.add(newRequest)
                            } else if(document.getBoolean("receiverExclude") != false){
                                requestManageReceivedList.add(newRequest)
                            }


                        }

                        binding.rvRequest.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvRequest.setHasFixedSize(true)
                        requestReceivedAdapter = RequestReceivedAdapter { request, select ->
                            optionSelect(request,select)
                        }
                        binding.rvRequest.adapter = requestReceivedAdapter
                        requestReceivedAdapter.updateRequests(requestReceivedList)

                        binding.rvAccessReceived.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvRequest.setHasFixedSize(true)
                        requestManageReceivedAdapter = RequestManageReceivedAdapter(requestManageReceivedList)
                        binding.rvAccessReceived.adapter = requestManageReceivedAdapter
                        requestManageReceivedAdapter.updateRequests(requestManageReceivedList)

                    }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}