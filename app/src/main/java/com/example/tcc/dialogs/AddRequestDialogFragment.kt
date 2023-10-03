package com.example.tcc.dialogs

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.tcc.databinding.FragmentAddPropertyDialogBinding
import com.example.tcc.databinding.FragmentAddRequestDialogBinding
import com.example.tcc.model.Property
import com.example.tcc.model.Request
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddRequestDialogFragment(private val request: Request?) : DialogFragment() {

    private var _binding: FragmentAddRequestDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val newRequest = Request(null, null, null, null,
        null, null)

    companion object {
        const val TAG = "addRequestDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRequestDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textTitle.text = "Solicitação de acesso"
        initClicks()
    }


    private fun initClicks() {
        binding.buttonBack.setOnClickListener {
            dismiss()
        }

        binding.buttonSubmit.setOnClickListener {
            val email = binding.editEmail.text.toString()

            if (email.isEmpty()) {
                val snackbar = Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {

                db.collection("Customer").whereEqualTo("email",email)
                    .get().addOnSuccessListener { documents ->

                        if(documents.isEmpty){
                            val snackbar = Snackbar.make(it, "Email não existe!", Snackbar.LENGTH_SHORT)
                            snackbar.setBackgroundTint(Color.RED)
                            snackbar.show()
                        } else {
                            db.collection("Customer").document(auth.uid.toString())
                                .get().addOnSuccessListener { document ->
                                    var status = "Enviado"
                                    var sender = auth.uid.toString()
                                    var receiver = documents.documents[0].id
                                    var accept = false
                                    var senderExclude = false
                                    var receiverExclude = false




                                }
                        }

                        addRequest(email)
                    }


            }
        }
    }

    private fun addRequest(email:String) {


    }

}