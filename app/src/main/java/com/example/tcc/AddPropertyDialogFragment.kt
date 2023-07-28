package com.example.tcc

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.tcc.databinding.FragmentAddAreaDialogBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddPropertyDialogFragment(private val property: Property?) : DialogFragment() {

    private var _binding: FragmentAddAreaDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val newProperty = Property(null, null, null)

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

        if (property != null) {
            binding.editName.setText(property.name.toString())
            binding.editArea.setText(property.dimension.toString())
            binding.editLocalization.setText(property.location.toString())
        }

        binding.textTitle.text = "Adicionar Propriedade"
        initClicks()
    }

    private fun initClicks() {
        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        binding.buttonSubmit.setOnClickListener {
            val name = binding.editName.text.toString()
            val dimension = binding.editArea.text.toString()
            val localization = binding.editLocalization.text.toString()

            if (name.isEmpty() || dimension.isEmpty() || localization.isEmpty()) {
                val snackbar = Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                if (property != null) {
                    newProperty.name = name
                    newProperty.dimension = dimension.toLong()
                    newProperty.location = localization
                    editProperty(property, newProperty)
                } else {
                    addProperty(name, dimension.toLong(), localization)
                }
            }
        }
    }

    private fun editProperty(oldProperty: Property, newProperty: Property) {
        val name = oldProperty.name
        val dimension = oldProperty.dimension
        val location = oldProperty.location

        db.collection("Property").whereArrayContains("users", auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        for (document in documents) {
                            if (document.get("name") == name &&
                                document.get("dimension") == dimension &&
                                document.get("localization") == location
                            ) {
                                db.collection("Property")
                                    .document(document.id)
                                    .update(
                                        mapOf(
                                            "name" to newProperty.name,
                                            "dimension" to newProperty.dimension,
                                            "localization" to newProperty.location
                                        )
                                    )
                            }
                        }
                    }
                }
            }
        dismiss()
    }

    private fun addProperty(name: String, dimension: Long, localization: String) {

        val users: ArrayList<String> = ArrayList()
        users.add(auth.currentUser?.uid.toString())

        val areas: ArrayList<String> = ArrayList()


        val propertyMap = hashMapOf(
            "name" to name,
            "localization" to localization,
            "dimension" to dimension,
            "dimension_left" to dimension,
            "users" to users,
            "areas" to areas
        )

        db.collection("Property").add(propertyMap).addOnCompleteListener {
            Log.d("db", "sucesso ao cadastrar!")
            binding.editName.setText("")
            binding.editArea.setText("")
            dismiss()

        }.addOnFailureListener {
            Log.d("db", "Falha!")
        }
    }
}