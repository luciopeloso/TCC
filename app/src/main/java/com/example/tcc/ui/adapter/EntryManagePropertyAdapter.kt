package com.example.tcc.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.model.Property
import com.example.tcc.R
import com.example.tcc.databinding.ItemEntryPropertyBinding
import com.example.tcc.model.ChildData
import com.example.tcc.ui.listeners.EntryListener

class EntryManagePropertyAdapter(val entrySelected: (Property, Int) -> Unit):
    RecyclerView.Adapter<EntryManagePropertyAdapter.MyViewHolder>() {

    private var propertyList: List<Property> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    companion object {
        const val SELECT_EDIT: Int = 1
        const val SELECT_NEXT: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEntryPropertyBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(propertyList[position], position)
    }

    override fun getItemCount() = propertyList.size

    fun updateProperties(list: List<Property>) {
        propertyList = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemEntryPropertyBinding) :
    RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n")
        fun bindData(property: Property, position: Int) {
            binding.textDescription.text = property.name
            binding.textDimension.text = "${property.dimension} hectares"
            binding.textLocation.text = property.location

            binding.buttonEdit.setOnClickListener {

                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()

                entrySelected(property, SELECT_EDIT)
            }

            binding.buttonGo.setOnClickListener {
                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()
                entrySelected(property, SELECT_NEXT)
            }
        }
    }

}