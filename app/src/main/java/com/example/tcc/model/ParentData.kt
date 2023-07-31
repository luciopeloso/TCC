package com.example.tcc.model

import com.example.tcc.helper.AppConstants

data class ParentData(
    val parentTitle:String?=null,
    var type:Int = AppConstants.Constants.PARENT,
    var subList: MutableList<ChildData>? = ArrayList(),
    var isExpanded:Boolean = false
)