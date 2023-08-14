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

class EntryManageVintageAdapter: RecyclerView.Adapter<EntryManageVintageAdapter.MyViewHolder>() {

    private lateinit var listener: EntryListener
    private var vintageList: List<Vintage> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntryManageVintageAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEntryVintageBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding,listener)
    }

    override fun getItemCount() = vintageList.size

    override fun onBindViewHolder(holder: EntryManageVintageAdapter.MyViewHolder, position: Int) {
        if(positionSelected == position){
            holder.selectedBg()
        } else {
            holder.unselectedBg()
        }
        holder.bindData(vintageList[position], position)
    }

    fun updateAreas(list: List<Vintage>) {
        vintageList = list
        notifyDataSetChanged()
    }

    fun attachListener(entryListener: EntryListener) {
        listener = entryListener
    }

    inner class MyViewHolder(private val binding: ItemEntryVintageBinding, val listener: EntryListener) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(vintage: Vintage, position: Int) {
            binding.textDescription.text = vintage.description
            binding.textBegin.text = vintage.begin
            binding.textEnd.text = vintage.end

            binding.entryVintage.setOnClickListener {

                if(positionSelected==position){
                    positionSelected= RecyclerView.NO_POSITION
                    listener.onListClick(false)
                    notifyDataSetChanged()

                }
                positionSelected = position
                listener.onListClick(true)
                notifyDataSetChanged()

            }
        }

        fun selectedBg(){
            binding.shapeConstraint.setBackgroundResource(R.drawable.text_view_selected_border)
        }

        fun unselectedBg(){
            binding.shapeConstraint.setBackgroundResource(R.drawable.text_view_border)
        }
    }
}