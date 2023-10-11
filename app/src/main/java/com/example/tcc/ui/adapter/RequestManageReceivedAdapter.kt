package com.example.tcc.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.databinding.ItemManageReceivedRequestBinding
import com.example.tcc.model.Customer
import com.example.tcc.model.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RequestManageReceivedAdapter(val list: MutableList<Request>):
    RecyclerView.Adapter<RequestManageReceivedAdapter.MyViewHolder>() {

    private var requestList : List<Request> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemManageReceivedRequestBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding)
    }

    override fun getItemCount() = requestList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(requestList[position], position)
    }

    fun updateRequests(updatedList: List<Request>) {
        requestList = updatedList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemManageReceivedRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(request: Request, position: Int) {

            db.collection("Customer").whereEqualTo("Sender", request.sender)
                .get().addOnSuccessListener { document ->
                    val name = document.documents[0].get("name")
                    val lastName = document.documents[0].get("lastName")
                    val accessType = document.documents[0].get("acessType")
                    val email = document.documents[0].get("email")

                    binding.textMessage.text = "${name.toString()} ${lastName.toString()}"
                    binding.textSenderEmail.text = email.toString()
                    binding.textSenderStatus.text = request.status
                    binding.textSenderType.text = accessType.toString()
                }
        }

    }


}