package com.octo.nickshulhin.ubus.database

import com.google.firebase.database.*
import com.octo.nickshulhin.ubus.listeners.OnDataReceivedListener
import java.util.*


/**
 * Created by nickshulhin on 24/3/18.
 */
object Connectivity {
    fun pushHookID(id: String){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("hooks").child(id)
        myRef.setValue("empty")
    }

    fun subscribeForHookID(id: String, listener: OnDataReceivedListener){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("hooks").child(id)
        myRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener.onDataReceived(dataSnapshot.getValue(String::class.java)!!)
            }

            override fun onCancelled(p0: DatabaseError?) {}
        })
    }
}