package com.example.connect

import android.app.AlertDialog
import android.app.LauncherActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connect.dao.Postdao
import com.example.connect.models.Postdata
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.security.AccessController.getContext
import kotlin.math.sign


class MainActivity : AppCompatActivity(), Ipostadapter {

    private lateinit var postdao: Postdao
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: Postadapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bam.setOnClickListener {
            val intent = Intent(this, Post::class.java)
            startActivity(intent)
        }
        setupview()
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }

    fun signout(view: View) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Connect")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to sign out ?")

        builder.setIcon(R.drawable.ic_baseline_info_24)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            Toast.makeText(this, "Signed out", Toast.LENGTH_LONG).show()
            sign()
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            //Toast.makeText(this, "clicked No", Toast.LENGTH_LONG).show()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
    private fun setupview() {
        postdao = Postdao()
        val postCollections = postdao.postcollections
        val query = postCollections.orderBy("createdat", Query.Direction.DESCENDING)
        val recyclerViewoptions =
            FirestoreRecyclerOptions.Builder<Postdata>().setQuery(query, Postdata::class.java)
                .build()

        adapter = Postadapter(recyclerViewoptions, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun sign() {
        FirebaseAuth.getInstance().signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()

        }
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        adapter.stopListening()
    }

    override fun onlikecliked(postid: String) {
        postdao.update(postid)
    }
}


