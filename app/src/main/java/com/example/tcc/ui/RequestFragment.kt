package com.example.tcc.ui

import android.app.AlertDialog
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

        //initAdapters()
        getEntries()

        binding.radioGroupEntries.setOnCheckedChangeListener { _,checkedId ->

            when(checkedId){
                R.id.radio_my_access -> {
                    binding.rvAccessReceived.visibility = View.VISIBLE
                    binding.rvAccessSended.visibility = View.GONE
                }
                R.id.radio_sended -> {
                    binding.rvAccessSended.visibility = View.VISIBLE
                    binding.rvAccessReceived.visibility = View.GONE
                }
            }

        }

        initClicks()

    }


    private fun optionSelect(request: Request, select: Int) {

        when(select){
            RequestReceivedAdapter.SELECT_REFUSE -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Recusar Solicitação")
                    .setMessage("Você confirma recusar a solicitação?")
                    .setPositiveButton("Sim") { dialog, which ->
                        db.collection("Request").whereEqualTo("receiver", request.receiver)
                            .get().addOnSuccessListener { document ->
                                db.collection("Request")
                                    .document(document.documents[0].id).update("status", "Recusado")
                            }

                        requestReceivedList.clear()
                        requestManageReceivedList.clear()
                        requestManageSendedList.clear()
                        //getEntries()
                    }
                    .setNeutralButton("voltar", null)
                    .show()
            }

            RequestReceivedAdapter.SELECT_ACCEPT -> {
                db.collection("Request").whereEqualTo("receiver", request.receiver)
                    .get().addOnSuccessListener { document ->
                        db.collection("Request")
                            .document(document.documents[0].id).update("accept", "sim")
                        db.collection("Request")
                            .document(document.documents[0].id).update("status", "Aprovado")
                    }

                includeSender("Property",request.sender!!)
                includeSender("Area",request.sender!!)
                includeSender("Vintage",request.sender!!)
                includeSender("Entry",request.sender!!)

            }
        }
    }

    private fun initClicks() {
        binding.buttonAdd.setOnClickListener {
            dialogAdd = AddRequestDialogFragment()
            dialogAdd.show(childFragmentManager, AddRequestDialogFragment.TAG)
        }
    }

    private fun includeSender(type: String, sender: String){
        db.collection(type).whereArrayContains("users", auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        for (document in documents) {
                            db.collection(type).document(document.id)
                                .update("users", FieldValue.arrayUnion(sender))
                        }
                    }
                }
            }
    }

    private fun initAdapterManageSended(){
        binding.rvAccessSended.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAccessSended.setHasFixedSize(true)
        requestManageSendedAdapter = RequestManageSendedAdapter(requestManageSendedList)
        binding.rvAccessSended.adapter = requestManageSendedAdapter
    }

    private fun initAdapterManageReceived(){
        binding.rvAccessReceived.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRequest.setHasFixedSize(true)
        requestManageReceivedAdapter = RequestManageReceivedAdapter(requestManageReceivedList)
        binding.rvAccessReceived.adapter = requestManageReceivedAdapter
    }

    private fun initAdapterReceived(){
        binding.rvRequest.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRequest.setHasFixedSize(true)
        requestReceivedAdapter = RequestReceivedAdapter { request, select ->
            optionSelect(request,select)
        }
        binding.rvRequest.adapter = requestReceivedAdapter
    }

    private fun getEntries() {
        db.collection("Request").whereEqualTo("receiver",auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    Log.d("db", "Receivers")
                    val documents = snapshot?.documents

                    if (documents != null) {

                        requestReceivedList.clear()

                        for (document in documents) {
                            val status = document.get("status").toString()
                            val accept = document.get("accept").toString()
                            val sender = document.get("sender").toString()
                            val receiver = document.get("receiver").toString()

                            val newRequest = Request(status, sender, receiver, accept)

                            if(document.get("status").toString() == "Enviado"){
                                requestReceivedList.add(newRequest)
                            } else {
                                requestManageReceivedList.add(newRequest)
                            }
                        }
                        initAdapterReceived()
                        initAdapterManageReceived()
                        requestReceivedAdapter.updateRequests(requestReceivedList)
                        requestManageReceivedAdapter.updateRequests(requestManageReceivedList)
                        Log.d("db", "size received: ${requestReceivedList.size} ")
                        Log.d("db", "size manage received: ${requestManageReceivedList.size} ")
                    }
                }
            }


        db.collection("Request").whereEqualTo("sender",auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    Log.d("db", "Senders")
                    val documents = snapshot?.documents
                    Log.d("db", "${documents}")
                    if (documents != null) {
                        requestReceivedList.clear()

                        for (document in documents) {

                            val status = document.get("status").toString()
                            val accept = document.get("accept").toString()
                            val sender = document.get("sender").toString()
                            val receiver = document.get("receiver").toString()

                            val newRequest = Request(
                                status, sender, receiver, accept)

                            requestReceivedList.add(newRequest)
                        }
                        initAdapterManageSended()
                        requestManageSendedAdapter.updateRequests(requestReceivedList)
                        Log.d("db", "received manage sender: ${requestReceivedList.size} ")

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