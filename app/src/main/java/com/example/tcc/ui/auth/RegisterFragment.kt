package com.example.tcc.ui.auth

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.example.tcc.R
import com.example.tcc.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import kotlin.concurrent.thread


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private  val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSpinner()
        initClicks()
    }

    private fun initClicks() {
        binding.buttonSignIn.setOnClickListener {

            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val name = binding.editFirstName.text.toString()
            val lastName = binding.editLastName.text.toString()
            val acessType = binding.spinnerAccessType.selectedItem.toString()

            if (email.isEmpty() ||
                password.isEmpty() ||
                lastName.isEmpty() ||
                name.isEmpty() ||
                acessType == "0") {
                val snackbar = Snackbar.make(it, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else if(password != binding.editRepeatPassword.text.toString()){
                val snackbar = Snackbar.make(it, "Senhas não são identicas!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { signIn ->

                    if(signIn.isSuccessful) {
                        var snackbar = Snackbar.make(it, "Sucesso ao cadastrar usuário!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                        //binding.editEmail.setText("")
                        //binding.editPassword.setText("")
                        dataBaseSubmit(name, lastName, email, acessType)
                        navigationToLogin()

                    }

                }.addOnFailureListener { exception ->
                    val snackbar = Snackbar.make(it, validateFields(exception), Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.BLUE)
                    snackbar.show()
                }
            }
        }

        binding.buttonBack.setOnClickListener{
            navigationToLogin()
        }
    }

    private fun loadSpinner(){
        val list = listOf("Selecione seu tipo de acesso", "Proprietário", "Gerente")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,list)

        binding.spinnerAccessType.adapter = adapter
    }

    private fun dataBaseSubmit(name: String,lastName: String,email:String,acessType:String){

        val custumerMap = hashMapOf(
            "name" to name,
            "lastName" to lastName,
            "email" to email,
            "acessType" to acessType
        )

        //auth.currentUser?.uid.toString()

        db.collection("Customer").document(auth.currentUser?.uid.toString())
            .set(custumerMap)
            .addOnCompleteListener{
                Log.d("db","sucesso ao cadastrar!")

                while(!it.isSuccessful){

                }

            }.addOnFailureListener{
                Log.d("db","Falha!")
            }

    }
    private fun navigationToLogin(){
        thread {
            Thread.sleep(1000)
            auth.signOut()
        }

        //auth.signOut()

        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }
    private fun validateFields(exception: Exception): String{
        val messageError = when(exception){
            is FirebaseAuthWeakPasswordException -> "Digite um senha com no mínimo 6 caracteres!"
            is FirebaseAuthInvalidCredentialsException -> "Digite um email válido!"
            is FirebaseAuthUserCollisionException -> "Email ja existente!"
            is FirebaseNetworkException -> "Não conexão com a internet"
            else -> "Erro ao cadastrar usuário!"

        }
        return messageError
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}