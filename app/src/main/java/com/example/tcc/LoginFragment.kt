package com.example.tcc

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.tcc.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if(currentUser != null){
            navigationToHome()
        }

        initClicks()
    }

    private fun initClicks() {
        binding.buttonLogin.setOnClickListener { view->
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                val snackbar = Snackbar.make(view,"Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ autendicate ->
                    if(autendicate.isSuccessful){
                        navigationToHome()
                    }
                }.addOnFailureListener {
                    val snackbar = Snackbar.make(view,"Erro ao fazer o login!", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }
        }

        binding.textRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

        }

    }

    private fun navigationToHome(){
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}