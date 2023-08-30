package com.example.tcc.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.R
import com.example.tcc.databinding.ItemEntryAreaBinding
import com.example.tcc.databinding.ItemEntryVintageBinding
import com.example.tcc.model.Area
import com.example.tcc.model.Vintage
import com.example.tcc.ui.listeners.EntryListener

class EntryManageVintageAdapter(val entrySelected: (Vintage, Int) -> Unit)
    : RecyclerView.Adapter<EntryManageVintageAdapter.MyViewHolder>() {

    private var vintageList: List<Vintage> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    companion object {
        const val SELECT_EDIT: Int = 1
        const val SELECT_NEXT: Int = 2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntryManageVintageAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEntryVintageBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding)
    }

    override fun getItemCount() = vintageList.size

    override fun onBindViewHolder(holder: EntryManageVintageAdapter.MyViewHolder, position: Int) {
        holder.bindData(vintageList[position], position)
    }

    fun updateVintages(list: List<Vintage>) {
        vintageList = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemEntryVintageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(vintage: Vintage, position: Int) {
            binding.textDescription.text = vintage.description
            binding.textBegin.text = vintage.begin
            binding.textEnd.text = vintage.end

            binding.buttonEdit.setOnClickListener {

                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()

                entrySelected(vintage, SELECT_EDIT)
            }

            binding.buttonGo.setOnClickListener {
                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()
                entrySelected(vintage, SELECT_NEXT)
            }

        }



    }
}