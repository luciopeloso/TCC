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
import com.example.tcc.model.Customer
import com.example.tcc.model.Property
import com.example.tcc.model.Request
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddRequestDialogFragment() : DialogFragment() {

    private var _binding: FragmentAddRequestDialogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val newRequest = Request(null, null, null, null)

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
            } else if(email == auth.currentUser?.uid.toString()) {
                val snackbar = Snackbar.make(it, "Não é possível mandar solicitação para seu email!", Snackbar.LENGTH_SHORT)
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
                            db.collection("Request").whereEqualTo("sender",auth.currentUser?.uid.toString())
                                .get().addOnSuccessListener { document ->

                                    var check = false

                                    for(i in document){
                                        if(i.get("receiver") == email){
                                            check = true
                                            val snackbar = Snackbar.make(it, "Solicitação Existente!", Snackbar.LENGTH_SHORT)
                                            snackbar.setBackgroundTint(Color.RED)
                                            snackbar.show()
                                        }
                                    }

                                    if(!check){

                                        val status = "Enviado"
                                        val sender = auth.uid.toString()
                                        val receiver = documents.documents[0].id
                                        val accept = false

                                        newRequest.status = status
                                        newRequest.sender = sender
                                        newRequest.receiver = receiver
                                        newRequest.accept = accept

                                        addRequest(newRequest)
                                    }
                                }
                        }
                    }
            }
        }
    }

    private fun addRequest(request:Request) {

        val requestMap = hashMapOf(
            "status" to request.status,
            "sender" to request.sender,
            "receiver" to request.receiver,
            "accept" to request.accept
        )

        db.collection("Request").add(requestMap).addOnCompleteListener {
            //Log.d("db", "sucesso ao cadastrar!")
            binding.editEmail.setText("")
            dismiss()
        }

    }

}