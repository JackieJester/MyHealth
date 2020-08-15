package ro.georgemarinescu.myhealth.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Conversation(var id: String= "",var idDoctor: String = "",var idPacient:String = "",
                        var messages: ArrayList<Message> = arrayListOf(), var doctor:Profile? = null ,
                        var pacient:Profile? = null)
