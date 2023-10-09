package com.example.tcc.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.databinding.ItemManageSendedRequestBinding
import com.example.tcc.model.Customer
import com.example.tcc.model.Request
import com.example.tcc.ui.listeners.EntryListener
import com.example.tcc.ui.listeners.RequestDeleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RequestManageSendedAdapter(val list: MutableList<Request>, custumer: Customer?):
    RecyclerView.Adapter<RequestManageSendedAdapter.MyViewHolder>() {

    private lateinit var listener: RequestDeleteListener

    private var requestList : List<Request> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemManageSendedRequestBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding)
    }

    override fun getItemCount() = requestList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(requestList[position], position)
    }

    fun attachListener(requestListener: RequestDeleteListener) {
        listener = requestListener
    }

    fun updateRequests(updatedList: List<Request>) {
        requestList = updatedList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemManageSendedRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(request: Request, position: Int) {

            db.collection("Customer").whereEqualTo("sender", request.sender)
                .get().addOnSuccessListener { document ->
                    val name = document.documents[0].get("name")
                    val lastName = document.documents[0].get("lastName")
                    val accessType = document.documents[0].get("acessType")
                    val email = document.documents[0].get("email")

                    if(request.sender == auth.uid){
                        binding.textShowSenderEmail.text = "Email"
                    }

                    binding.textMessage.text = "${name.toString()} ${lastName.toString()}"
                    binding.textSenderEmail.text = email.toString()
                    binding.textSenderStatus.text = request.status
                }



        }

    }


}