package com.quizo.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.jaeger.library.StatusBarUtil
import com.quizo.R
import com.quizo.utils.Updates
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class ProfileActivity:AppCompatActivity() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = firebaseAuth.currentUser
    private var googleSignInClient: GoogleSignInClient? = null
    val ID = Objects.requireNonNull(currentUser)!!.displayName
    var tv:TextView? = null
    var emai:TextView? = null
    var btn:MaterialCardView? = null
    var prof:CircleImageView? = null
    var publisher:MaterialCardView? = null
    var updates:MaterialCardView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profilr)
        StatusBarUtil.setTransparent(this)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.btmnav)
        bottomNavigationView.selectedItemId = R.id.page_4
        bottomNavigationView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    startActivity(Intent(applicationContext, FeedsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_1 -> {startActivity(Intent(applicationContext, LoggedIn::class.java))
                    overridePendingTransition(0, 0)}

                R.id.page_4 -> {
                    return@OnNavigationItemSelectedListener true

                }

            }
            false
        })

          val toolbar:MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.prof_menu)
        supportActionBar?.title = " "
        val profU:Uri? = currentUser!!.photoUrl
        tv = findViewById(R.id.textView)
        emai = findViewById(R.id.textView2)
        btn = findViewById(R.id.c2)
        publisher = findViewById(R.id.c1)
        updates = findViewById(R.id.c12)
        prof = findViewById(R.id.profimg)
        prof?.visibility = View.INVISIBLE
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_fade_in)
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val bundle = intent.extras
        if(prof != null) {
            Picasso.get().load(profU).fit().into(prof)

            if (bundle!=null && bundle.getString("loc")=="from_logged"){
                Handler().postDelayed({
                    prof?.visibility = View.VISIBLE
                    prof?.startAnimation(bounceAnimation)
                },100)

            }else{
                prof?.visibility = View.VISIBLE
            prof?.startAnimation(bounceAnimation)
            }
        }else{
            Picasso.get().load("https://cdn.business2community.com/wp-content/uploads/2017/08/blank-profile-picture-973460_640.png").fit().into(prof)
        }
        tv?.text = ID
        emai?.text = currentUser.email
        publisher?.setOnClickListener {
            Toast.makeText(applicationContext,"This option is still under alpha programme.Please contact developer for more details",Toast.LENGTH_SHORT).show()
        }
        updates?.setOnClickListener {
            startActivity(Intent(this, Updates::class.java))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }
        btn?.setOnClickListener{
            signOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.prof_menu,menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
                when(item?.itemId) {
                R.id.about -> {startActivity(Intent(this, AboutActivity::class.java))}
                R.id.rate -> {
                    val url = "https://play.google.com/store/apps/details?id=com.quizo"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)}
                R.id.feedback -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "venumanas87@gmail.com"))
                        intent.putExtra(Intent.EXTRA_SUBJECT, "FEEDBACK ($ID)")
                        intent.putExtra(Intent.EXTRA_TEXT, ">>>")
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this@ProfileActivity, "Sorry...You don't have any mail app", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                    }
                }
                    return super.onOptionsItemSelected(item)

    }
    private fun signOut() {
        // Firebase sign out
        firebaseAuth.signOut()
        // Google sign out
        googleSignInClient!!.signOut().addOnCompleteListener(this
        ) { // Google Sign In failed, update UI appropriately
            loggedout()
            Log.w(LoggedIn.TAG, "Signed out of google")
        }
    }


    private fun loggedout() {
        val inte = Intent(this, SecondActivity::class.java)
        this.startActivity(inte)
        finish()
    }
    override fun onPostResume() {
        super.onPostResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.btmnav)
        bottomNavigationView!!.selectedItemId = R.id.page_4
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAfterTransition()

    }
}