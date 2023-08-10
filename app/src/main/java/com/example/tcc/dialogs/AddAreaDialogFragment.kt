package com.example.tcc.dialogs

import android.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.tcc.databinding.FragmentAddAreaDialogBinding
import com.example.tcc.databinding.FragmentAddPropertyDialogBinding
import com.example.tcc.model.Area
import com.example.tcc.model.Property
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddAreaDialogFragment(private val area: Area?, private val propertyID: String?) :
    DialogFragment() {

    private var _binding: FragmentAddAreaDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val newArea = Area(null, null, null)

    companion object {
        const val TAG = "addAreaDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAreaDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = listOf("Selecione a cultura", "Café", "Soja", "Milho")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, list)

        binding.spinnerCrop.adapter = adapter

        if (area != null) {

            val selected = area.crop.toString()
            val spinnerPosition: Int = adapter.getPosition(selected)
            binding.spinnerCrop.setSelection(spinnerPosition)
            binding.editName.setText(area.name.toString())
            binding.editArea.setText(area.dimension.toString())

        }

        binding.textTitle.text = "Adicionar talhão"
        initClicks()
    }

    private fun initClicks() {
        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        binding.buttonSubmit.setOnClickListener {
            val name = binding.editName.text.toString()
            val dimension = binding.editArea.text.toString()
            val crop = binding.spinnerCrop.selectedItem.toString()

            if (name.isEmpty() || dimension.isEmpty() || crop == "0") {
                val snackbar = Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                if (area != null) {
                    newArea.name = name
                    newArea.dimension = dimension.toLong()
                    newArea.crop = crop
                    editArea(area, newArea)
                } else {
                    addArea(name, dimension.toLong(), crop)
                }
            }
        }
    }


    private fun editArea(oldArea: Area, newArea: Area) {
        val name = oldArea.name
        val dimension = oldArea.dimension
        val crop = oldArea.crop

        db.collection("Property").document(propertyID.toString())
            .get().addOnSuccessListener { document ->

                db.collection("Property").document(propertyID.toString())
                    .update("dimension_left", FieldValue.increment(dimension!!))

                if (newArea.dimension!!.toLong() > document.getLong("dimension_left")!!) {
                    Toast.makeText(requireContext(), "dimensão não compatível!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    db.collection("Area").whereArrayContains("users", auth.currentUser?.uid.toString())
                        .addSnapshotListener { snapshot, e ->
                            if (e == null) {
                                val documents = snapshot?.documents
                                if (documents != null) {
                                    for (document in documents) {
                                        if (document.get("name") == name &&
                                            document.get("dimension") == dimension &&
                                            document.get("crop") == crop
                                        ) {
                                            db.collection("Area")
                                                .document(document.id)
                                                .update(
                                                    mapOf(
                                                        "name" to newArea.name,
                                                        "dimension" to newArea.dimension,
                                                        "crop" to newArea.crop
                                                    )
                                                )
                                            db.collection("Property").document(propertyID.toString())
                                                .update("dimension_left", FieldValue.increment(-dimension))

                                            binding.editName.setText("")
                                            binding.editArea.setText("")
                                            dismiss()
                                        }
                                    }
                                }
                            }
                        }
                }
            }
    }

    private fun addArea(name: String, dimension: Long, crop: String) {

        val users: ArrayList<String> = ArrayList()
        users.add(auth.currentUser?.uid.toString())

        val vintages: ArrayList<String> = ArrayList()


        val propertyMap = hashMapOf(
            "name" to name,
            "crop" to crop,
            "dimension" to dimension,
            "property" to propertyID,
            "users" to users,
            "vintages" to vintages
        )

        db.collection("Property").document(propertyID.toString())
            .get().addOnSuccessListener { document ->

                if (dimension > document.getLong("dimension_left")!!) {
                    Toast.makeText(requireContext(), "dimensão não compatível!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    db.collection("Area").add(propertyMap)
                        .addOnSuccessListener { documentReference ->
                            db.collection("Property").document(propertyID.toString())
                                .update("areas", FieldValue.arrayUnion(documentReference.id))
                                .addOnSuccessListener {

                                    db.collection("Property").document(propertyID.toString())
                                        .update("dimension_left", FieldValue.increment(-dimension))

                                    binding.editName.setText("")
                                    binding.editArea.setText("")
                                    dismiss()
                                }
                        }.addOnFailureListener {
                            //Log.d("db", "Falha!")
                        }
                }
            }
    }

}


