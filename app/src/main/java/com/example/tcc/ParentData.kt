package com.example.tcc

data class ParentData(
    val parentTitle:String?=null,
    var type:Int = AppConstants.Constants.PARENT,
    var subList: MutableList<ChildData>? = ArrayList(),
    var isExpanded:Boolean = false
)