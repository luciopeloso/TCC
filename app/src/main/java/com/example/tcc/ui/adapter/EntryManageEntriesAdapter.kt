package com.example.tcc.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.tcc.helper.AppConstants
import com.example.tcc.model.ChildData
import com.example.tcc.model.ParentData
import com.example.tcc.R
import com.example.tcc.databinding.ItemEntryChildBinding
import com.example.tcc.databinding.ItemEntryVintageBinding
import com.example.tcc.model.Vintage
import com.example.tcc.ui.listeners.EntryListener
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent

class EntryManageEntriesAdapter(val list: MutableList<ParentData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var listener: EntryListener

    private var _entryList: List<ParentData> = mutableListOf()
    private var entryList = list

    var positionSelected = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType== AppConstants.Constants.PARENT){
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_entry_parent, parent,false)
            GroupViewHolder(rowView)
        } else {
            //val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_entry_child, parent,false)
            //ChildViewHolder(rowView)

            val inflater = LayoutInflater.from(parent.context)
            val itemBinding = ItemEntryChildBinding.inflate(inflater, parent, false)
            return ChildViewHolder(itemBinding,listener)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dataList = list[position]
        if (dataList.type == AppConstants.Constants.PARENT) {
            holder as GroupViewHolder
            holder.apply {
                parentTV?.text = dataList.parentTitle
                component?.setOnClickListener{
                    expandOrCollapseParentItem(dataList,position)
                }
            }
        } else {
            holder as ChildViewHolder

            holder.apply {

                val singleService = dataList.subList?.first()

                //childTV?.text = singleService.childTitle

                if(positionSelected == position){
                    holder.selectedBg()
                } else {
                    holder.unselectedBg()
                }
                if (singleService != null) {
                    holder.bindData(singleService, position)
                }
            }
        }
    }
    fun expandOrCollapseParentItem(singleBoarding: ParentData, position: Int) {

        if (singleBoarding.isExpanded) {
            collapseParentRow(position)
        } else {
            expandParentRow(position)
        }
    }

    fun expandParentRow(position: Int){
        val currentBoardingRow = list[position]
        val services = currentBoardingRow.subList
        currentBoardingRow.isExpanded = true
        var nextPosition = position
        if(currentBoardingRow.type== AppConstants.Constants.PARENT){

            services?.forEach { service ->
                val parentModel =  ParentData()
                parentModel.type = AppConstants.Constants.CHILD
                val subList : ArrayList<ChildData> = ArrayList()
                subList.add(service)
                parentModel.subList=subList
                list.add(++nextPosition,parentModel)
            }
            notifyDataSetChanged()
        }
    }

    fun collapseParentRow(position: Int){
        val currentBoardingRow = list[position]
        val services = currentBoardingRow.subList
        list[position].isExpanded = false
        if(list[position].type== AppConstants.Constants.PARENT){
            services?.forEach { _ ->
                list.removeAt(position + 1)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int = list[position].type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun attachListener(entryListener: EntryListener) {
        listener = entryListener
    }

    fun updateEntries(updatedList: List<ParentData>) {
        entryList = updatedList.toMutableList()
        notifyDataSetChanged()
    }

    inner class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val component = row.findViewById(R.id.item_parent) as ConstraintLayout?
        val parentTV = row.findViewById(R.id.parent_Title) as TextView?
        val downIV  = row.findViewById(R.id.down_iv) as ImageView?
    }
    inner class ChildViewHolder(private val binding: ItemEntryChildBinding, val listener: EntryListener)
        : RecyclerView.ViewHolder(binding.root) {

        //val childTV = row.findViewById(R.id.entry_child) as TextView?
        /*val type = row.findViewById(R.id.parent_Title) as TextView?
        val description = row.findViewById(R.id.text_description) as TextView?
        val unity = row.findViewById(R.id.text_unity) as TextView?
        val price = row.findViewById(R.id.text_price) as TextView?
        val total = row.findViewById(R.id.text_total) as TextView?*/

        @SuppressLint("SetTextI18n")
        fun bindData(entry: ChildData, position: Int) {

            binding.textDescription.text = entry.description
            binding.textQuantity.text = entry.quantity.toString()
            binding.textUnity.text = entry.unity
            binding.textPrice.text = entry.price.toString()
            binding.textTotal.text = entry.total.toString()

            binding.entryChild.setOnClickListener {

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
