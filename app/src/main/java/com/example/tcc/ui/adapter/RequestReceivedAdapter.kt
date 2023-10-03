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

class RequestReceivedAdapter(val requestSelected: (Request, Int) -> Unit):
    RecyclerView.Adapter<RequestReceivedAdapter.MyViewHolder>() {


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
            /*binding.textMessage.text = "${request.name} ${request.lastName} solicitou acesso aos seus dados."
            binding.textSenderEmail.text = request.email
            binding.textSenderType.text = request.accessLevel
            if(request.type == 2L){
                binding.textShowSenderEmail.text = "Email"
            }

            binding.imageAccept.setOnClickListener {
                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()

                requestSelected(request, RequestReceivedAdapter.SELECT_ACCEPT)
            }

            binding.imageExclude.setOnClickListener {
                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()
                requestSelected(request, RequestReceivedAdapter.SELECT_REFUSE)
            }
        */
        }

    }




}