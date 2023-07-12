package com.example.tcc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class EntryAdapter(var mContext: Context, val list: MutableList<ParentData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private  val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType== AppConstants.Constants.PARENT){
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.entry_parent_adapter, parent,false)
            GroupViewHolder(rowView)
        } else {
            val rowView: View = LayoutInflater.from(parent.context).inflate(R.layout.entry_child_adapter, parent,false)
            ChildViewHolder(rowView)
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
            }
        }
    }
    private fun expandOrCollapseParentItem(singleBoarding: ParentData,position: Int) {

        if (singleBoarding.isExpanded) {
            collapseParentRow(position)
        } else {
            expandParentRow(position)
        }
    }

    private fun expandParentRow(position: Int){
        val currentBoardingRow = list[position]
        val services = currentBoardingRow.subList
        currentBoardingRow.isExpanded = true
        var nextPosition = position
        if(currentBoardingRow.type==AppConstants.Constants.PARENT){

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

    private fun collapseParentRow(position: Int){
        val currentBoardingRow = list[position]
        val services = currentBoardingRow.subList
        list[position].isExpanded = false
        if(list[position].type==AppConstants.Constants.PARENT){
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

    class GroupViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        val component = row.findViewById(R.id.item_parent) as ConstraintLayout?
        val parentTV = row.findViewById(R.id.parent_Title) as TextView?
        val downIV  = row.findViewById(R.id.down_iv) as ImageView?
    }
    class ChildViewHolder(row: View) : RecyclerView.ViewHolder(row) {
        //val childTV = row.findViewById(R.id.entry_child) as TextView?
        val description = row.findViewById(R.id.text_description) as TextView?
        val unity = row.findViewById(R.id.text_unity) as TextView?
        val price = row.findViewById(R.id.text_price) as TextView?
        val total = row.findViewById(R.id.text_total) as TextView?


    }


}
