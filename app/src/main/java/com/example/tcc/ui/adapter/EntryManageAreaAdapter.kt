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

class EntryManageAreaAdapter(val entrySelected: (Area, Int) -> Unit):
    RecyclerView.Adapter<EntryManageAreaAdapter.MyViewHolder>() {

    private var areaList: List<Area> = mutableListOf()

    var positionSelected = RecyclerView.NO_POSITION

    companion object {
        const val SELECT_EDIT: Int = 1
        const val SELECT_NEXT: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEntryAreaBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: EntryManageAreaAdapter.MyViewHolder, position: Int) {
        holder.bindData(areaList[position], position)
    }

    override fun getItemCount() = areaList.size

    fun updateAreas(list: List<Area>) {
        areaList = list
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemEntryAreaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindData(area: Area, position: Int) {
            binding.textDescription.text = area.name
            binding.textDimension.text = "${area.dimension} hectares"
            binding.textCulture.text = area.crop

            binding.buttonEdit.setOnClickListener {

                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()

                entrySelected(area, SELECT_EDIT)
            }

            binding.buttonGo.setOnClickListener {
                if(positionSelected==position){
                    positionSelected=RecyclerView.NO_POSITION
                    notifyDataSetChanged()
                }
                positionSelected = position
                notifyDataSetChanged()
                entrySelected(area, SELECT_NEXT)
            }
        }
    }
}