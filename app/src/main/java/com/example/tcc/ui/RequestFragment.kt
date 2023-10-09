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

    private lateinit var  requestReceiverAdapter : RequestManageReceivedAdapter
    private lateinit var  requestManageAdapter : RequestReceivedAdapter
    private lateinit var  requestSendedAdapter : RequestManageSendedAdapter

    private val requestManageReceiverList = mutableListOf<Request>()
    private val requestManageSendedList = mutableListOf<Request>()
    private val requestSendedList = mutableListOf<Request>()

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
        binding.rvAccessReceived.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRequest.setHasFixedSize(true)
        requestReceiverAdapter = RequestManageReceivedAdapter(requestManageReceiverList,receiverCustomer)
        binding.rvAccessReceived.adapter = requestReceiverAdapter

        binding.rvAccessSended.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAccessSended.setHasFixedSize(true)
        requestSendedAdapter = RequestManageSendedAdapter(requestManageSendedList,senderCustomer)
        binding.rvAccessSended.adapter = requestSendedAdapter
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
        db.collection("Request").whereEqualTo("sender",auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        requestSendedList.clear()

                        for (document in documents) {
                            if(document.get("status").toString() == "Enviado"){
                                val status = document.get("status").toString()
                                val accept = document.getBoolean("accept")
                                val senderExclude = document.getBoolean("senderExlude")
                                val receiverExclude = document.getBoolean("receiverExclude")
                                val sender = document.get("sender").toString()
                                val receiver = document.get("receiver").toString()

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


                                //Log.d("db", "ID: ${document.id}  DADOS: ${document.data}")
                                val newRequest = Request(status, sender, receiver, accept as Boolean,
                                    senderExclude as Boolean, receiverExclude as Boolean)
                                requestSendedList.add(newRequest)
                            }
                        }
                        binding.rvRequest.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvRequest.setHasFixedSize(true)
                        requestManageAdapter = RequestReceivedAdapter { request, select ->
                            optionSelect(request,select)
                        }
                        binding.rvRequest.adapter = requestManageAdapter
                        requestManageAdapter.updateRequests(requestSendedList)
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