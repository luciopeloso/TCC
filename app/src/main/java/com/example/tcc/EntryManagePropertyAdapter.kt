package com.example.tcc

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.databinding.ItemEntryPropertyBinding

class EntryManagePropertyAdapter:
    RecyclerView.Adapter<EntryManagePropertyAdapter.MyViewHolder>() {

    private lateinit var listener: EntryListener
    private var propertyList: List<Property> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEntryPropertyBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding,listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(positionSelected == position){
            holder.selectedBg()
        } else {
            holder.unselectedBg()
        }
        holder.bindData(propertyList[position], position)
    }

    override fun getItemCount() = propertyList.size

    fun updateProperties(list: List<Property>) {
        propertyList = list
        notifyDataSetChanged()
    }

    fun attachListener(entryListener: EntryListener) {
        listener = entryListener
    }

    inner class MyViewHolder(private val binding: ItemEntryPropertyBinding, val listener: EntryListener) :
    RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n")
        fun bindData(property: Property, position: Int) {
            binding.textDescription.text = property.name
            binding.textDimension.text = "${property.dimension} hectares"

            binding.entryProperty.setOnClickListener {

                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
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