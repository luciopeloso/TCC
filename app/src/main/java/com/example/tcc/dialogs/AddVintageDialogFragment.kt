package com.example.tcc.dialogs

import android.R
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.tcc.databinding.FragmentAddAreaDialogBinding
import com.example.tcc.databinding.FragmentAddVintageDialogBinding
import com.example.tcc.databinding.FragmentVintageManagerBinding
import com.example.tcc.model.Area
import com.example.tcc.model.Vintage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar

class AddVintageDialogFragment(private val vintage: Vintage?, private val areaID: String?): DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentAddVintageDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    private val newVintage = Vintage(null, null, null)

    companion object {
        const val TAG = "addVintageDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddVintageDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (vintage != null) {
            binding.editDescription.setText(vintage.description.toString())
            binding.editBegin.setText(vintage.begin.toString())
            binding.editEnd.setText(vintage.end.toString())
        }

        binding.textTitle.text = "Adicionar safra"
        initClicks()
    }

    private fun initClicks() {
        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        binding.buttonSubmit.setOnClickListener {

            //val dateBegin = SimpleDateFormat("yyyy-MM-dd").parse(begin)
            //binding.editBegin.setText(SimpleDateFormat("dd/MM/yyyy").format(dateBegin))

            val description = binding.editDescription.text.toString()
            val begin = binding.editBegin.text.toString()
            val end = binding.editEnd.text.toString()

            if (description.isEmpty() || begin.isEmpty() || end.isEmpty()) {
                val snackbar = Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                if (vintage != null) {
                    newVintage.description = description
                    newVintage.begin = begin
                    newVintage.end = end
                    editVintage(vintage, newVintage)
                } else {
                    addVintage(description, begin, end)
                }
            }
        }

        binding.editBegin.setOnClickListener {
            handleDate()
        }

        binding.editEnd.setOnClickListener {
            handleDate()
        }

    }

    private fun editVintage(oldVintage: Vintage, newVintage: Vintage) {
        val description = oldVintage.description
        val begin = oldVintage.begin
        val end = oldVintage.end

        db.collection("Property").document(areaID.toString())
            .get().addOnSuccessListener { document ->
                    db.collection("Vintage").whereArrayContains("users", auth.currentUser?.uid.toString())
                        .addSnapshotListener { snapshot, e ->
                            if (e == null) {
                                val documents = snapshot?.documents
                                if (documents != null) {
                                    for (document in documents) {
                                        if (document.get("description") == description &&
                                            document.get("begin") == begin &&
                                            document.get("end") == end
                                        ) {
                                            db.collection("Vintage")
                                                .document(document.id)
                                                .update(
                                                    mapOf(
                                                        "description" to newVintage.description,
                                                        "begin" to newVintage.begin,
                                                        "end" to newVintage.end
                                                    )
                                                )
                                            binding.editDescription.setText("")
                                            binding.editBegin.setText("")
                                            binding.editEnd.setText("")
                                            dismiss()
                                        }
                                    }
                                }
                            }
                        }
            }
    }

    private fun addVintage(description: String, begin: String, end: String) {

        val users: ArrayList<String> = ArrayList()
        users.add(auth.currentUser?.uid.toString())

        val entries: ArrayList<String> = ArrayList()


        val propertyMap = hashMapOf(
            "description" to description,
            "begin" to begin,
            "end" to end,
            "property" to areaID,
            "users" to users,
            "entries" to entries
        )

        db.collection("Area").document(areaID.toString())
            .get().addOnSuccessListener { document ->
                db.collection("Vintage").add(propertyMap)
                    .addOnSuccessListener { documentReference ->
                        db.collection("Area").document(areaID.toString())
                            .update("vintages", FieldValue.arrayUnion(documentReference.id))
                            .addOnSuccessListener {
                                binding.editDescription.setText("")
                                binding.editBegin.setText("")
                                binding.editEnd.setText("")
                                dismiss()
                            }
                    }.addOnFailureListener {
                        //Log.d("db", "Falha!")
                    }

            }
    }

    override fun onDateSet(v: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        val dueDate = dateFormat.format(calendar.time)
        //binding.edit.text = dueDate
    }

    private fun handleDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(), this, year, month, day).show()
    }

}