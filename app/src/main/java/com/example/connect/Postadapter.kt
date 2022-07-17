package com.example.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.connect.Utils.Utils.Companion.getTimeAgo
import com.example.connect.models.Postdata
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Postadapter(options: FirestoreRecyclerOptions<Postdata>,val listner: Ipostadapter) : FirestoreRecyclerAdapter<Postdata, Postadapter.Postviewholder>(
    options
) {
    class Postviewholder(itemview:View): RecyclerView.ViewHolder(itemview){

        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Postviewholder {
        val viewHolder =  Postviewholder(LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false))
          viewHolder.likeButton.setOnClickListener{
               listner.onlikecliked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
          }
        return viewHolder
    }

    override fun onBindViewHolder(holder: Postviewholder, position: Int, model: Postdata) {
        holder.postText.text = model.text
        holder.userText.text = model.createdby.displayname
        Glide.with(holder.userImage.context).load(model.createdby.imageurl).circleCrop().into(holder.userImage)
        holder.likeCount.text = model.likedby.size.toString()
        holder.createdAt.text=Utils.Utils.getTimeAgo(model.createdat)
        val auth =Firebase.auth
        val currentuser= auth.currentUser!!.uid
        val isliked= model.likedby.contains(currentuser)
        if(isliked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked))
        }
         else{

            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_notliked))
        }
    }

}
interface Ipostadapter{
    fun onlikecliked(postid:String)
}