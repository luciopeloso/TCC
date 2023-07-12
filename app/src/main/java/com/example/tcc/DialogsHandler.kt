package com.example.tcc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tcc.databinding.FragmentEntriesBinding

class DialogsHandler(): Fragment() {

    fun handleAddAreaDialog(){
        val dialog = AddDialogFragment()
        dialog.show(childFragmentManager, dialog.tag)
    }


}