package com.example.tcc.dialogs


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.tcc.R
import com.example.tcc.databinding.FragmentAddEntriesDialogBinding
import com.example.tcc.databinding.FragmentAddVintageDialogBinding
import com.example.tcc.databinding.FragmentEntriesManagerBinding
import com.example.tcc.model.ChildData
import com.example.tcc.model.Vintage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat

class AddEntriesDialogFragment(private val entry: ChildData?, private val vintageID: String?) :
    DialogFragment() {

    private var _binding: FragmentAddEntriesDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var newEntry = ChildData(null, null, null, null, null, null, null)

    companion object {
        const val TAG = "addEntriesDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEntriesDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listType = listOf(
            "Tipo de entrada",
            "Produto",
            "Sementes",
            "Capina, dessecação e pós emergência",
            "Fungicidas, Inseticidas e Foliares",
            "Operações"
        )

        val adapter =
            ArrayAdapter(requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, listType)

        binding.spinnerCategory.adapter = adapter

        if (entry != null) {

            val selected = entry.category.toString()
            val spinnerPosition: Int = adapter.getPosition(selected)
            binding.spinnerCategory.setSelection(spinnerPosition)
            binding.editDescription.setText(entry.description.toString())
            binding.editQuantity.setText(entry.quantity.toString())
            binding.editUnity.setText(entry.unity.toString())
            binding.editPrice.setText(entry.price.toString())
            setType()
        }

        binding.textTitle.text = "Adicionar Entrada"
        initClicks()
    }

    private fun setType() {
        binding.radioGroupEntries.check(
            when(entry?.type){
                0L -> {
                    R.id.radio_budgeted
                } else -> {
                    R.id.radio_accomplished
                }
            }
        )
    }

    private fun initClicks() {
        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        binding.buttonSubmit.setOnClickListener {

            val category = binding.spinnerCategory.selectedItem.toString()
            val description = binding.editDescription.text.toString()
            val quantity = binding.editQuantity.text.toString()
            val unity = binding.editUnity.text.toString()
            val price = binding.editPrice.text.toString()

            if (category == "0" || description.isEmpty() || quantity.isEmpty()
                || unity.isEmpty() || price.isEmpty()
            ) {
                val snackbar = Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                if (entry != null) {
                    newEntry.description = description
                    newEntry.category = category
                    newEntry.quantity = quantity.toLong()
                    newEntry.unity = unity
                    newEntry.price = price.toLong()
                    newEntry.type = 0L

                    if(binding.radioBudgeted.isChecked){
                        newEntry.type = 0L
                    } else {
                        newEntry.type = 1L
                    }

                    editEntry(entry, newEntry)
                } else {
                    val buttonSelected = if(binding.radioBudgeted.isChecked){ 0 } else { 1 }
                    addEntry(category, description, quantity.toFloat(), unity, price.toFloat(), buttonSelected)
                }
            }
        }
    }

    private fun addEntry(category: String, description: String, quantity: Float, unity: String, price: Float, type: Int) {
        val users: ArrayList<String> = ArrayList()
        users.add(auth.currentUser?.uid.toString())

        val total = (quantity * price)

        val entryMap = hashMapOf(
            "description" to description,
            "category" to category,
            "quantity" to quantity,
            "unity" to unity,
            "price" to price,
            "type" to type,
            "total" to total,
            "vintage" to vintageID,
            "users" to users
        )

        db.collection("Entry").add(entryMap)
            .addOnSuccessListener { documentReference ->
                db.collection("Vintage").document(vintageID.toString())
                    .update("entries", FieldValue.arrayUnion(documentReference.id))
                    .addOnSuccessListener {
                        binding.editDescription.setText("")
                        binding.editPrice.setText("")
                        binding.editUnity.setText("")
                        dismiss()
                    }
            }.addOnFailureListener {
                //Log.d("db", "Falha!")
            }
    }

    private fun editEntry(oldEntry: ChildData, newEntry: ChildData) {
        val description = oldEntry.description
        val category = oldEntry.category
        val quantity = oldEntry.quantity
        val unity = oldEntry.unity
        val price = oldEntry.price
        val type = oldEntry.type

        db.collection("Entry").whereArrayContains("users", auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        for (document in documents) {
                            if (document.get("description") == description &&
                                document.get("category") == category &&
                                document.get("quantity") == quantity &&
                                document.get("unity") == unity &&
                                document.get("price") == price &&
                                document.get("type") == type
                            ) {
                                db.collection("Entry")
                                    .document(document.id)
                                    .update(
                                        mapOf(
                                            "description" to newEntry.description,
                                            "type" to newEntry.type,
                                            "category" to newEntry.category,
                                            "unity" to newEntry.unity,
                                            "quantity" to newEntry.category,
                                            "price" to newEntry.price,
                                            "total" to 0
                                        )
                                        //(newEntry.price!! * newEntry.quantity!!)
                                    )

                                binding.editDescription.setText("")
                                binding.editPrice.setText("")
                                binding.editUnity.setText("")
                                dismiss()
                            }
                        }
                    }
                }
            }
    }

}

