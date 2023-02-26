package com.anksys.mishatodoapp.model

import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*


data class ToDoItem (var id:String="" , var title:String="", var status:Boolean=false, var timestamp:String=""){

    public fun statusChanged(){
        status=!status

}}