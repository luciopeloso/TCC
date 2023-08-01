package com.example.tcc.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.R
import com.example.tcc.databinding.ItemEntryAreaBinding
import com.example.tcc.databinding.ItemEntryPropertyBinding
import com.example.tcc.model.Area
import com.example.tcc.model.Property
import com.example.tcc.ui.listeners.EntryListener

class EntryManageAreaAdapter:
    RecyclerView.Adapter<EntryManageAreaAdapter.MyViewHolder>() {

    private lateinit var listener: EntryListener
    private var areaList: List<Area> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEntryAreaBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding,listener)
    }

    override fun onBindViewHolder(holder: EntryManageAreaAdapter.MyViewHolder, position: Int) {
        if(positionSelected == position){
            holder.selectedBg()
        } else {
            holder.unselectedBg()
        }
        holder.bindData(areaList[position], position)
    }

    override fun getItemCount() = areaList.size

    fun updateAreas(list: List<Area>) {
        areaList = list
        notifyDataSetChanged()
    }

    fun attachListener(entryListener: EntryListener) {
        listener = entryListener
    }

    inner class MyViewHolder(private val binding: ItemEntryAreaBinding, val listener: EntryListener) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(area: Area, position: Int) {
            binding.textDescription.text = area.name
            binding.textDimension.text = "${area.dimension} hectares"
            binding.textCulture.text = area.crop

            binding.entryArea.setOnClickListener {

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