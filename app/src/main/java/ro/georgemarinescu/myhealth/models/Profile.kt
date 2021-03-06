package ro.georgemarinescu.myhealth.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Profile(val id:String = "", var surname:String= "", var name:String= "", var phone:String= "", val doctor:Boolean= false, val specialisation:String = "", var previous:Float = 0F, var count:Int = 0)

