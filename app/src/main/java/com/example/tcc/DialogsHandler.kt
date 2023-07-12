package com.example.tcc


import androidx.fragment.app.Fragment


class DialogsHandler(): Fragment() {

    fun handleAddAreaDialog(){
        val dialog = AddDialogFragment()
        dialog.show(childFragmentManager, AddDialogFragment.TAG)
    }


}