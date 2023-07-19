package com.example.tcc

class Property(val name: String,
               val dimension: Long,
               val listCustomer: MutableList<String>? = mutableListOf(),
               val listAreas: MutableList<String>? = mutableListOf(),
               var selected: Boolean = false)
