package com.example.connect.dao

import com.example.connect.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    // Data access object
    private val db = FirebaseFirestore.getInstance()
    private val userscollection = db.collection("users")
     fun useradd(user: User?){
         user?.let {
             GlobalScope.launch(Dispatchers.IO) {
                 userscollection.document(user.uid).set(it)  // we want to add user in backgorund thread
             }                                               //with out stopping the ui for a moment
         }
     }
    fun getuserbyId(uId: String): Task<DocumentSnapshot>{
        return userscollection.document(uId).get()
    }
}