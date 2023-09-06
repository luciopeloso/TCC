package com.example.tcc.dialogs


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
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
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AddEntriesDialogFragment(private val entry: ChildData?, private val vintageID: String?) :
    DialogFragment() {

    private var _binding: FragmentAddEntriesDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var newEntry = ChildData(null, null, null, null, null, null, null)


    companion object {
        const val TAG = "addEntriesDialog"
        private const val replaceRegex: String = "[R$,.\u00A0]"
        private const val replaceFinal: String = "\u00A0"
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

        binding.editPrice.moneyTextWatch()

        if (entry != null) {

            val selected = entry.category.toString()
            val spinnerPosition: Int = adapter.getPosition(selected)
            binding.spinnerCategory.setSelection(spinnerPosition)
            binding.editDescription.setText(entry.description.toString())
            binding.editQuantity.setText(entry.quantity.toString())
            binding.editUnity.setText(entry.unity.toString())
            binding.editPrice.setText(String.format("%.2f", entry.price).replace(".", ","))
            //binding.editPrice.setText(entry.price.toString())
            setType()
        }

        binding.textTitle.text = "Adicionar Entrada"
        initClicks()
    }

    private fun setType() {
        binding.radioGroupEntries.check(
            when(entry?.type){
                "Orçado" -> {
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
            var quantity = binding.editQuantity.text.toString()
            val unity = binding.editUnity.text.toString()
            var price = binding.editPrice.text.toString()

            if (category == "Tipo de entrada" || description.isEmpty() || quantity.isEmpty()
                || unity.isEmpty() || price.isEmpty()
            ) {
                val snackbar = Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                if (entry != null) {
                    newEntry.description = description
                    newEntry.category = category
                    newEntry.quantity = quantity.toDouble()
                    newEntry.unity = unity
                    newEntry.price = price.toDouble()
                    newEntry.type = "Orçado"

                    if(binding.radioBudgeted.isChecked){
                        newEntry.type = "Orçado"
                    } else {
                        newEntry.type = "Realizado"
                    }

                    editEntry(entry, newEntry)
                } else {
                    val buttonSelected = if(binding.radioBudgeted.isChecked){ "Orçado" } else { "Realizado" }
                    quantity = quantity.replace(",",".")
                    price = price.replace(",",".")
                    price = price.replace("R$","")

                    addEntry(category, description, quantity.toDouble(), unity, price.toDouble(), buttonSelected)
                }
            }
        }
    }

    private fun addEntry(category: String, description: String, quantity: Double, unity: String, price: Double, type: String) {
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
        val total = (quantity?.times(price!!))

        db.collection("Entry").whereArrayContains("users", auth.currentUser?.uid.toString())
            .addSnapshotListener { snapshot, e ->
                if (e == null) {
                    val documents = snapshot?.documents
                    if (documents != null) {
                        for (document in documents) {
                            if (document.get("description") == description &&
                                document.get("category") == category &&
                                document.getDouble("quantity") == quantity &&
                                document.get("unity") == unity &&
                                document.getDouble("price") == price &&
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
                                            "total" to total
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

    private fun EditText.moneyTextWatch(){
        this.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                try{
                    val stringEditable = editable.toString()
                    if(stringEditable.isEmpty()) return
                    removeTextChangedListener(this)
                    val cleanString = stringEditable.replace(replaceRegex.toRegex(), "")

                    val parsed = BigDecimal(cleanString)
                        .setScale(2)
                        .divide(BigDecimal(100))
                    val decimalFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR")) as DecimalFormat
                    val formatted = decimalFormat.format(parsed)

                    val stringFinal = formatted.replace(replaceFinal, "")
                    setText(stringFinal)
                    setSelection(stringFinal.length)
                    addTextChangedListener(this)

                } catch (e: Exception){
                    Log.d("ERROR", e.toString())
                }
            }

        })
    }

}

