package com.example.tcc.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.databinding.ItemEntryPropertyBinding
import com.example.tcc.databinding.ItemManageReceivedRequestBinding
import com.example.tcc.databinding.ItemReceivedRequestBinding
import com.example.tcc.model.Property
import com.example.tcc.model.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RequestReceivedAdapter(val requestSelected: (Request, Int) -> Unit):
    RecyclerView.Adapter<RequestReceivedAdapter.MyViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var requestList: List<Request> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    companion object {
        const val SELECT_ACCEPT: Int = 1
        const val SELECT_REFUSE: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemReceivedRequestBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding)
    }

    override fun getItemCount() = requestList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(requestList[position], position)
    }

    fun updateRequests(updatedList: List<Request>) {
        requestList = updatedList.toMutableList()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemReceivedRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(request: Request, position: Int) {
            db.collection("Customer").whereEqualTo("Receiver", request.receiver)
                .get().addOnSuccessListener { document ->
                    val name = document.documents[0].get("name")
                    val lastName = document.documents[0].get("lastName")
                    val accessType = document.documents[0].get("acessType")
                    val email = document.documents[0].get("email")

                    binding.textMessage.text = "${name.toString()} ${lastName.toString()}"
                    binding.textSenderEmail.text = email.toString()
                    binding.textSenderType.text = accessType.toString()
                }

            binding.imageAccept.setOnClickListener {
                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()

                requestSelected(request, SELECT_ACCEPT)
            }

            binding.imageRefuse.setOnClickListener {
                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()
                requestSelected(request, SELECT_REFUSE)
            }

        }

    }




}