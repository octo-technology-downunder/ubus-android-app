package com.octo.nickshulhin.ubus.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.octo.nickshulhin.ubus.listeners.OnDataReceivedListener
import com.octo.nickshulhin.ubus.model.DataModel
import okhttp3.*
import java.io.IOException
import okhttp3.RequestBody


/**
 * Created by nickshulhin on 24/3/18.
 */
object Connectivity {
    val database = FirebaseDatabase.getInstance()


    fun pushLocation(dataModel: DataModel, listener: OnDataReceivedListener<String>) {
        val client = OkHttpClient()
        val body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                "{\"start_lat\":\" " + dataModel.startLat + "\", \"start_long\": \"" + dataModel.startLong + "\", \"end_lat\": \"" + dataModel.endLat + "\", \"end_long\": \"" + dataModel.endLong + "\"}")
        val request = Request.Builder()
                .post(body)
                .url("https://ubus-client-api.herokuapp.com/trip")
                .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                listener.onDataReceived(response.body().toString())
            }

            override fun onFailure(call: Call, e: IOException) {
                listener.onDataReceived("FAIL!")
            }
        })
    }

    fun subscribeForHookID(id: String, listener: OnDataReceivedListener<String>) {
        val myRef = database.getReference("hooks").child(id)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    listener.onDataReceived(dataSnapshot.getValue(String::class.java)!!)
                }
            }

            override fun onCancelled(p0: DatabaseError?) {}
        })
    }
}