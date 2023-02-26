package com.anksys.mishatodoapptest.model
data class ToDoItem (var id:String="" , var title:String="", var status:Boolean=false, var desc:String="",var timestamp:String="", var imgUri: String){

    public fun statusChanged(){
        status=!status

}}