package com.example.tcc

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.databinding.ItemEntryPropertyBinding

class EntryManagePropertyAdapter(private val context: Context,
                                 private val propertyList: List<Property>
                                 ):
    RecyclerView.Adapter<EntryManagePropertyAdapter.MyViewHolder>() {

    private lateinit var listener: EntryListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemEntryPropertyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = propertyList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val property = propertyList[position]

        holder.binding.textDescription.text = property.name
        holder.binding.textDimension.text = "${property.dimension} hectares"

        holder.binding.entryProperty.setOnClickListener{
            if(!property.selected){
                property.selected = true
                holder.binding.shapeConstraint.setBackgroundResource(R.drawable.text_view_selected_border)
                listener.onListClick(true)
            }
        }

    }

    fun attachListener(entryListener: EntryListener) {
        listener = entryListener
    }

    inner class MyViewHolder(val binding: ItemEntryPropertyBinding) :
        RecyclerView.ViewHolder(binding.root)
}