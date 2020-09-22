package com.quizo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.first_time.*
import java.util.*


class FirstTime:AppCompatActivity() {
    var gen:String? = null
    var firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser
    val id = Objects.requireNonNull(currentUser)!!.displayName
    val fn = id?.split(" ")?.get(0)
    private val email = currentUser!!.email
    val db = Firebase.firestore
    var firedb = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor  = Color.parseColor("#222222")
        setContentView(R.layout.first_time)
        val male_iv:ImageView = findViewById(R.id.male)
        val female_iv:ImageView = findViewById(R.id.female)
        val usetTV:TextView = findViewById(R.id.user)
        usetTV.text = "Hey $fn,"
        male_iv.visibility = View.GONE
        female_iv.visibility = View.INVISIBLE
        checkExistence()
        startAnimate()
    }



    private fun checkExistence() {
        val refDB = db.collection("users").document(email.toString())

              refDB.get()
                      .addOnSuccessListener {documentSnapshot ->
                          if (documentSnapshot.exists()){
                             //finish()
                              Toast.makeText(applicationContext,"EXISTS",Toast.LENGTH_SHORT).show()
                          }
                      }

    }


    fun handleClick(view:View) {
        when(view.id){
            R.id.male -> {
                gen = "male"
                finish()
            }
            R.id.female -> {
                gen = "female"
                finish()
            }
            R.id.ng -> {
                gen = "ng"
                finish()
            }
        }
        val gender = hashMapOf<String,Any?>(
                "Gender" to gen,
                "Name" to id
        )
         db.collection("users").document(email.toString())
                 .set(gender)
                 .addOnSuccessListener { Log.d(TAG, "handleClick: Successfull Registration")}
                 .addOnFailureListener { Log.d(TAG, "handleClick: failedddddd") }
    }

    private fun start() {
        val i = Intent(this,LoggedIn::class.java)
        startActivity(i)
    }


    private fun startAnimate() {
        val male_iv:ImageView = findViewById(R.id.male)
        val female_iv:ImageView = findViewById(R.id.female)
        val ng:TextView = findViewById(R.id.ng)
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
         Handler().postDelayed({
             male_iv.visibility = View.VISIBLE
             male_iv.startAnimation(bounceAnimation)

             female_iv.visibility = View.VISIBLE
             female_iv.startAnimation(bounceAnimation)
         },1000)

    }


    companion object {
        const val TAG = "FirsTime"
    }
}