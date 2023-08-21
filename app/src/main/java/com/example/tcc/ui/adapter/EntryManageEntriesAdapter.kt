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
import com.example.tcc.dialogs.AddAreaDialogFragment
import com.example.tcc.dialogs.AddEntriesDialogFragment
import com.example.tcc.model.Vintage
import com.example.tcc.ui.listeners.EntryListener
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent

class EntryManageEntriesAdapter(val list: MutableList<ChildData>) : RecyclerView.Adapter<EntryManageEntriesAdapter.MyViewHolder>() {

    private lateinit var listener: EntryListener

    private var _entryList: List<ParentData> = mutableListOf()
    private var entryList = list

    var positionSelected = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EntryManageEntriesAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemEntryChildBinding.inflate(inflater, parent, false)
        return MyViewHolder(itemBinding,listener)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: EntryManageEntriesAdapter.MyViewHolder, position: Int) {
        if(positionSelected == position){
            //holder.selectedBg()
        } else {
            //holder.unselectedBg()

        }
        holder.bindData(entryList[position], position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun attachListener(entryListener: EntryListener) {
        listener = entryListener
    }

    fun updateEntries(updatedList: List<ChildData>) {
        entryList = updatedList.toMutableList()
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemEntryChildBinding, val listener: EntryListener)
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
                    positionSelected = RecyclerView.NO_POSITION
                    listener.onListClick(false)
                    notifyDataSetChanged()

                } else {
                    positionSelected = position
                    listener.onListClick(true)
                    notifyDataSetChanged()
                }
            }

        }
    }


}
