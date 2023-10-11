package com.example.tcc.ui

import android.app.AlertDialog
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
import com.google.firebase.firestore.FieldValue
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


    private fun optionSelect(request: Request, select: Int) {

        when(select){
            RequestReceivedAdapter.SELECT_REFUSE -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Recusar Solicitação")
                    .setMessage("Você confirma recusar a solicitação?")
                    .setPositiveButton("Sim") { dialog, which ->
                        db.collection("Request").whereEqualTo("Receiver", request.receiver)
                            .get().addOnSuccessListener { document ->
                                db.collection("Request")
                                    .document(document.documents[0].id).update("accept", "Recusado")
                            }

                        requestReceivedList.clear()
                        getEntries()
                    }
                    .setNeutralButton("voltar", null)
                    .show()
            }

            RequestReceivedAdapter.SELECT_ACCEPT -> {
                db.collection("Request").whereEqualTo("Receiver", request.receiver)
                    .get().addOnSuccessListener { document ->
                        db.collection("Request")
                            .document(document.documents[0].id).update("accept", "Aprovado")
                    }

                /*db.collection("Property").document(request.receiver.toString())
                    .update("users", FieldValue.arrayUnion(request.receiver.toString()))*/

            }
        }
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
                            val sender = document.get("sender").toString()
                            val receiver = document.get("receiver").toString()

                            val newRequest = Request(status, sender, receiver, accept as Boolean)

                            if(document.get("status").toString() == "Enviado"){
                                requestReceivedList.add(newRequest)
                            } else {
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


        db.collection("Request").whereEqualTo("Sender",auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        requestReceivedList.clear()

                        for (document in documents) {

                            val status = document.get("status").toString()
                            val accept = document.getBoolean("accept")
                            val sender = document.get("sender").toString()
                            val receiver = document.get("receiver").toString()

                            val newRequest = Request(
                                status, sender, receiver, accept as Boolean)

                            requestManageSendedList.add(newRequest)


                        }

                        binding.rvAccessSended.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvAccessSended.setHasFixedSize(true)
                        requestManageSendedAdapter = RequestManageSendedAdapter(requestManageSendedList)
                        binding.rvAccessSended.adapter = requestManageSendedAdapter
                        requestManageSendedAdapter.updateRequests(requestReceivedList)

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