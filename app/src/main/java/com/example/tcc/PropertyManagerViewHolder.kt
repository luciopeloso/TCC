package com.example.tcc

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.databinding.ItemEntryPropertyBinding

class PropertyManagerViewHolder(private val binding: ItemEntryPropertyBinding, val listener: EntryListener) :
RecyclerView.ViewHolder(binding.root) {

    private var positionSelected = RecyclerView.NO_POSITION
    private var positionActual = RecyclerView.NO_POSITION



    @SuppressLint("SetTextI18n")
    fun bindData(property: Property, propertyList: List<Property>, position: Int) {
        binding.textDescription.text = property.name
        binding.textDimension.text = "${property.dimension} hectares"

        binding.entryProperty.setOnClickListener{

//            if(!property.selected){
//                positionSelected = position
//                positionActual = position
//
//                if(positionSelected != position){
//                    propertyList[positionSelected].selected = false
//                }
//
//                property.selected = true
//                listener.onListClick(true)
//            } else {
//                property.selected = false
//                listener.onListClick(false)
//            }
        }
    }

    fun selectedBg(){
        binding.shapeConstraint.setBackgroundResource(R.drawable.text_view_selected_border)
    }

    fun unselectedBg(){
        binding.shapeConstraint.setBackgroundResource(R.drawable.text_view_border)
    }
}