package com.example.connect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.connect.dao.UserDao
import com.example.connect.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



class SignIn : AppCompatActivity() {
    private val RC_SIGN_IN: Int = 123
    private lateinit var auth: FirebaseAuth
   private val TAG= " SignInActivity Tag"
    private lateinit var googleSignInClient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        // Configure Google Sign In
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
     signin.setOnClickListener{
         signIn()
     }
    }
// this function check if the user is already signed in or not , if not it make them sign in
    override fun onStart() {
        super.onStart()
        val currentuser= auth.currentUser
        updatedUI(currentuser)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
           handlesignInresult(task)
        }
    }

    private fun handlesignInresult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
              firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
       val credential = GoogleAuthProvider.getCredential(idToken,null)   // Global scope , work in background thread, a coroutine
      signin.visibility=View.GONE
        progressbar.visibility=View.VISIBLE

        GlobalScope.launch(Dispatchers.IO){
         val auth = auth.signInWithCredential(credential).await()
         val firebaseUser = auth.user
         withContext(Dispatchers.Main){
             updatedUI(firebaseUser)        // with context main dispatch switches from background thread to main thread
         }
     }
    }

    private fun updatedUI(firebaseUser: FirebaseUser?) {
         if (firebaseUser!= null){
             val user = User(firebaseUser.uid, firebaseUser.displayName.toString(), firebaseUser.photoUrl.toString())
             val userDao = UserDao()
             userDao.useradd(user)

             val mainActivityIntent = Intent(this,MainActivity::class.java)
             startActivity(mainActivityIntent)
             finish()
         }
        else{
             signin.visibility=View.VISIBLE
             progressbar.visibility=View.GONE
         }
    }
}
