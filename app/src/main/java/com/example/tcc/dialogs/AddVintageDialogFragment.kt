package com.example.tcc.dialogs

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.tcc.databinding.FragmentAddAreaDialogBinding
import com.example.tcc.databinding.FragmentVintageManagerBinding
import com.example.tcc.model.Area
import com.example.tcc.model.Vintage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddVintageDialogFragment(private val vintage: Vintage?, private val areaID: String?): DialogFragment() {

    private var _binding: FragmentVintageManagerBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val newVintage = Vintage(null, null, null)

    companion object {
        const val TAG = "addVintageDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVintageManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (vintage != null) {

            /*val selected = area.crop.toString()
            val spinnerPosition: Int = adapter.getPosition(selected)
            binding.spinnerCrop.setSelection(spinnerPosition)
            binding.editName.setText(area.name.toString())
            binding.editArea.setText(area.dimension.toString())*/

        }

        //binding. .text = "Adicionar Propriedade"
        initClicks()
    }

    private fun initClicks() {
        TODO("Not yet implemented")
    }

}