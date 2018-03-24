package com.octo.nickshulhin.ubus.database

import com.google.firebase.database.*
import com.octo.nickshulhin.ubus.listeners.OnDataReceivedListener
import com.octo.nickshulhin.ubus.model.DataModel
import java.util.*


/**
 * Created by nickshulhin on 24/3/18.
 */
object Connectivity {
    val database = FirebaseDatabase.getInstance()


    fun pushLocation(dataModel: DataModel) {
        val myRef = database.getReference("locations").child(dataModel.uuid)
        myRef.setValue(dataModel)
    }

    fun subscribeForHookID(id: String, listener: OnDataReceivedListener<String>){
        val myRef = database.getReference("hooks").child(id)
        myRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    listener.onDataReceived(dataSnapshot.getValue(String::class.java)!!)
                }
            }

            override fun onCancelled(p0: DatabaseError?) {}
        })
    }
}