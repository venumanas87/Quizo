package com.quizo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.jaeger.library.StatusBarUtil
import com.kwabenaberko.openweathermaplib.constants.Units
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather
import com.quizo.*
import com.quizo.R
import com.quizo.adapters.DataAdapter
import com.quizo.objects.*
import com.quizo.utils.Constants
import com.quizo.utils.StateNameConverter
import com.quizo.utils.prettyCount
import com.quizo.viewmodel.CovidViewModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import it.sephiroth.android.library.xtooltip.ClosePolicy
import it.sephiroth.android.library.xtooltip.Tooltip
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.acl.Owner
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class LoggedIn : AppCompatActivity(),CoroutineScope {
    var job:Job = Job()
  override val coroutineContext:CoroutineContext = Dispatchers.Main + job
    var LAT = 0.0
    var LON = 0.0
    var JDATA: String? = null
    var weather: LottieAnimationView? = null
    var PERMISSION_ID = 44
    var mFusedLocationClient: FusedLocationProviderClient? = null
    var firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser
    val ID = Objects.requireNonNull(currentUser)!!.displayName
    private val email = currentUser!!.email
    val db = Firebase.firestore
    var googleSignInClient: GoogleSignInClient? = null
    var statee:String? = ""
    var deceasedC:String? = ""
    var tooltip: Tooltip? = null
    var tooltip1: Tooltip? = null
    lateinit var cvm:CovidViewModel
    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        lastLocation()
        super.onCreate(savedInstanceState)
        checkExistence()
        FirebaseMessaging.getInstance().subscribeToTopic("All")
        setContentView(R.layout.logged)
        val ref = findViewById<LottieAnimationView>(R.id.refresh)
        val stcard:MaterialCardView = findViewById(R.id.stCard)
        val iny = findViewById<TextView>(R.id.inyour)
        cvm = ViewModelProvider(this).get(CovidViewModel::class.java)
        GlobalScope.launch {
            cvm.fetchData()
        }
        stcard.setOnClickListener {
            openCovid(stcard)
        }
        val bottomNavigationView:BottomNavigationView = findViewById<BottomNavigationView>(R.id.btmnav)
        bottomNavigationView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    startActivity(Intent(applicationContext, FeedsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_1 -> return@OnNavigationItemSelectedListener true

                R.id.page_4 -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                }

            }
            false
        })
        ref.setOnClickListener {
            ref.playAnimation()
            lastLocation()
        }

        StatusBarUtil.setTransparent(this)
        val main = findViewById<TextView>(R.id.main)
        val profimg:CircleImageView = findViewById(R.id.profimg)
        Picasso.get().load(currentUser?.photoUrl.toString()).into(profimg)
        profimg.setOnClickListener {
            val i = Intent(this, ProfileActivity::class.java)
            i.putExtra("loc","from_logged")
            this.startActivity(i)
            overridePendingTransition(R.anim.screen_slide_in_from_right, R.anim.screen_slide_out_to_left)
        }
        main.text = "$ID !"
        main.setOnClickListener { chat() }
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fadein)
        main.startAnimation(animation)
        //findViewById(R.id.buttonDisconnect).setOnClickListener(this);
        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        firebaseAuth = FirebaseAuth.getInstance()
        expand()
        saved()
    }



    private fun expand() {
        val CoronaCard = findViewById<MaterialCardView>(R.id.coroCD)
        val expL = findViewById<LinearLayout>(R.id.expandL)
        val image = findViewById<ImageView>(R.id.img)
        image.setOnClickListener {
            if (expL.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(CoronaCard, AutoTransition())
                expL.visibility = View.VISIBLE
                val rotate = RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 500
                rotate.interpolator = LinearInterpolator()
                rotate.fillAfter = true
                image.startAnimation(rotate)
            } else {
                TransitionManager.beginDelayedTransition(CoronaCard, AutoTransition())
                expL.visibility = View.GONE
                val rotate = RotateAnimation(180F, 0F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                rotate.duration = 500
                rotate.interpolator = LinearInterpolator()
                rotate.fillAfter = true
                image.startAnimation(rotate)
            }
        }
    }
    private fun extrD(JSD: CovidData) {
        runOnUiThread {
            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
            val datalist: MutableList<CoData> = ArrayList()
            val mAdapter = DataAdapter(datalist)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = mAdapter
            for (i in JSD.states.indices){
                JSD.states[i].let {
                    datalist.add(CoData("",it.stateName,prettyCount.pretty(it.total.deceased),prettyCount.pretty(it.total.recovered),prettyCount.pretty(it.total.confirmed),"m"))
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun weather(Lat: Double, Lon: Double) {
        val helper = OpenWeatherMapHelper("236011d2dcb15854914e1db09e019d42")
        helper.setUnits(Units.METRIC)
        helper.getCurrentWeatherByGeoCoordinates(Lat, Lon, object : CurrentWeatherCallback {
            override fun onSuccess(currentWeather: CurrentWeather?) {
                val deg = findViewById<TextView>(R.id.deg)
                val city = findViewById<TextView>(R.id.city)
                val desc = findViewById<TextView>(R.id.desc)
                val wind = findViewById<TextView>(R.id.wind)
                val d = currentWeather!!.weather[0].description
                val icon = currentWeather.weather[0].icon
                val de = currentWeather.main.tempMax
                val degr = de.toInt()
                val builder = StringBuilder();
                builder.append(degr).append("\u2103")
                deg.setText(builder.toString())
                city.text = currentWeather.name + "," + currentWeather.sys.country
                desc.text = currentWeather.weather[0].description
                wind.text = "Wind Speed: " + currentWeather.wind.speed
                Log.v(TAG, """
     Coordinates: ${currentWeather.coord.lat}, ${currentWeather.coord.lon}
     Weather Description: ${currentWeather.weather[0].description}
     Temperature: ${currentWeather.main.tempMax}
     Wind Speed: ${currentWeather.wind.speed}
     City, Country: ${currentWeather.name}, ${currentWeather.sys.country}
     """)
                wicon(icon)
               job = launch(Dispatchers.IO){ geo(Lat, Lon)}
            }

            override fun onFailure(throwable: Throwable) {
                Log.v(TAG, throwable.message)
            }
        })
    }

    private fun geo(lat: Double, lon: Double) {

        val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.isNotEmpty()) {
                val state = addresses[0].adminArea
                Log.d(TAG, "geo: STATE =$state")
                this.statee = state
                runOnUiThread {
                    ripData(state)
                }


            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun wicon(d: String) {
        weather = findViewById(R.id.anim)
        when (d) {
            "01d" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_Stdaec.json")
                weather?.playAnimation()
            }
            "01n" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_y6mY2A.json")
                weather?.playAnimation()
            }
            "02d", "03d" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_dgjK9i.json")
                weather?.playAnimation()
            }
            "02n", "03n" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_Jj2Qzq.json")
                weather?.playAnimation()
            }
            "04d", "04n" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_VAmWRg.json")
                weather?.playAnimation()
            }
            "09d", "10d" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_rpC1Rd.json")
                weather?.playAnimation()
            }
            "09n", "10n" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_I5XMi9.json")
                weather?.playAnimation()
            }
            "11d", "11n" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_XkF78Y.json")
                weather?.playAnimation()
            }
            "13d" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_BSVgyt.json")
                weather?.playAnimation()
            }
            "13n" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_RHbbn6.json")
                weather?.playAnimation()
            }
            "50d", "50n" -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_kOfPKE.json")
                weather?.playAnimation()
            }
            else -> {
                weather?.setAnimationFromUrl("https://assets4.lottiefiles.com/temp/lf20_Stdaec.json")
                weather?.playAnimation()

            }
        }
    }

 fun openCovid(view:View){
     if(this.statee!=""){
     val i = Intent(applicationContext, CovidFullActivity::class.java)
         i.putExtra("cont",this.statee)
         i.putExtra("dec",this.deceasedC)
     startActivity(i)
     overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
 }else{
         Toast.makeText(applicationContext,"Please Retry",Toast.LENGTH_SHORT).show()
     }
 }
    private fun loggedout() {
        val inte = Intent(this, SecondActivity::class.java)
        this.startActivity(inte)
        finish()
    }

    fun chat() {
        val intex = Intent(this, NewsActivity::class.java)
        this.startActivity(intex)
    }

  fun saved() {
      val ref = findViewById<LottieAnimationView>(R.id.refresh)
      val iny = findViewById<TextView>(R.id.inyour)
        val dataS = getSharedPreferences("tooltips", 0)
        if (dataS.getString("firstimes", "") == "no") { // first run is happened
        } else { //  this is the first run of application


            tooltip = Tooltip.Builder(this)
                    .anchor(ref, 0, 0, false)
                    .text("Click here to refresh")
                    .arrow(true)
                    .floatingAnimation(Tooltip.Animation.SLOW)
                    .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
                    .showDuration(30000)
                    .overlay(true)
                    .create()
            tooltip1 = Tooltip.Builder(this)
                    .anchor(iny, 0, 0, false)
                    .text("Click to view full stats")
                    .arrow(true)
                    .floatingAnimation(Tooltip.Animation.SLOW)
                    .closePolicy(ClosePolicy.TOUCH_ANYWHERE_CONSUME)
                    .showDuration(30000)
                    .overlay(true)
                    .create()



            ref.post {  tooltip!!.doOnHidden {
                iny.post {
                    tooltip1!!.doOnHidden { }
                            .doOnFailure { }
                            .doOnShown { }
                            .show(iny, Tooltip.Gravity.BOTTOM, true)
                }
            }
                    .doOnFailure { }
                    .doOnShown { }
                    .show(ref, Tooltip.Gravity.BOTTOM, true)
            }



            val editor = dataS.edit()
            editor.putString("firstimes", "no")
            editor.apply()
        }
    }

    @SuppressLint("MissingPermission")
    private fun lastLocation() {
            if (checkPermissions()) {
                if (isLocationEnabled) {
                    mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            LAT = location.latitude
                            LON = location.longitude
                            Log.d(TAG, "onComplete: $LAT$LON")
                            weather(LAT, LON)
                        }
                    }
                } else {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }
        }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }

    private val isLocationEnabled: Boolean
        private get() {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
            )
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lastLocation()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            lastLocation()
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.btmnav)
        bottomNavigationView.selectedItemId = R.id.page_1
    }


    override fun onPostResume() {
        super.onPostResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.btmnav)
        bottomNavigationView.selectedItemId = R.id.page_1
    }

    @SuppressLint("SetTextI18n")
    fun ripData(state:String){
        val iny = findViewById<TextView>(R.id.inyour)
        iny.text = state
                cvm.data.observe(this,
                        androidx.lifecycle.Observer {
                            if (it == "E") {
                                Toast.makeText(applicationContext, "COVID19 Server Busy", Toast.LENGTH_SHORT).show()
                            } else {
                                cvm.covidData.observe(this, androidx.lifecycle.Observer {
                                    extrD(it)
                                    var conI = 0
                                    var decI = 0
                                    var recI = 0
                                    var actI = 0
                                    for (i in it.states.indices) {
                                        val sta = it.states[i]
                                        if (it.states[i].stateName == state) {
                                            val c = findViewById<TextView>(R.id.conf)
                                            val r = findViewById<TextView>(R.id.rec)
                                            val d = findViewById<TextView>(R.id.dea)

                                            c.text = " ${prettyCount.pretty(sta.total.confirmed)} \n (+${prettyCount.pretty(sta.delta.confirmed)})"
                                            r.text = " ${prettyCount.pretty(sta.total.recovered)} \n (+${prettyCount.pretty(sta.delta.recovered)})"
                                            d.text = "${prettyCount.pretty(sta.total.deceased)} \n (+${prettyCount.pretty(sta.delta.recovered)})"
                                        }
                                        conI += sta.total.confirmed / 2
                                        decI += sta.total.deceased / 2
                                        recI += sta.total.recovered / 2
                                        var condI = 0
                                        var decdI = 0
                                        var recdI = 0
                                        condI += sta.delta.confirmed/2
                                        decdI += sta.delta.deceased/2
                                        recdI += sta.delta.recovered/2

                                        val c1 = findViewById<TextView>(R.id.conf1)
                                        val r1 = findViewById<TextView>(R.id.rec1)
                                        val d1 = findViewById<TextView>(R.id.dea1)

                                        c1.text = "${prettyCount.pretty(conI)}\n(+${prettyCount.pretty(condI)})"
                                        r1.text = "${prettyCount.pretty(recI)}\n(+${prettyCount.pretty(recdI)})"
                                        d1.text = "${prettyCount.pretty(decI)}\n(+${prettyCount.pretty(decdI)})"


                                    }
                                })
                            }
                        })

    }








    private fun checkExistence() {
        val refDB = db.collection("users").document(email.toString())
        refDB.get()
                .addOnSuccessListener {  documentSnapshot ->
                    if (!documentSnapshot.exists()){
                        startActivity(Intent(applicationContext, FirstTimeActivity::class.java))
                    }
                }

    }

    companion object {
        const val TAG = "MainActivity"
        private const val ARG_NAME = "username"
        @JvmStatic
        fun startActivity(context: Context, username: String?) {
            val intent = Intent(context, LoggedIn::class.java)
            intent.putExtra(ARG_NAME, username)
            context.startActivity(intent)
        }
    }
}