package com.example.connect.dao

import com.example.connect.Post
import com.example.connect.models.Postdata
import com.example.connect.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Postdao {
    val db = FirebaseFirestore.getInstance()
    val postcollections = db.collection("posts")
    val auth = Firebase.auth
    fun addpost(text: String) {
        val currentuserId =
            auth.currentUser!!.uid // !! ensures that there is user to sign if something // goes off the app crahses returning null pointer
        GlobalScope.launch {
            val userDao = UserDao()
            val user = userDao.getuserbyId(currentuserId).await().toObject(User::class.java)!!

            val currenttime = System.currentTimeMillis()
            val post = Postdata(text, user, currenttime)
            postcollections.document().set(post)
        }
    }

    fun getPostbyId(postid: String): Task<DocumentSnapshot> {
        return postcollections.document(postid).get()
    }

    fun update(postid: String) {
        GlobalScope.launch {
            val currentuserId = auth.currentUser!!.uid
            val post = getPostbyId(postid).await().toObject(Postdata::class.java)
            val isliked = post!!.likedby.contains(currentuserId)
            if (isliked) {
                post.likedby.remove(currentuserId)
            } else {
                post.likedby.add(currentuserId)
            }
            postcollections.document(postid).set(post)
        }
    }
}