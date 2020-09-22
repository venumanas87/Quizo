package com.quizo

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.shared_view.*
import java.io.File
import java.io.FileOutputStream

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SharedeleClass : AppCompatActivity() {
    //var ischecked = false
    var save: LottieAnimationView? = null
    var idForSaveView: RelativeLayout? = null
    var tv1: TextView? = null
    var tv2: TextView? = null
    var auth: String? = null
    var vi: Vibrator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.shared_view)
        idForSaveView = findViewById(R.id.rl2)
        MobileAds.initialize(this) { }
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        var user = "NOTNOT"
        var desc = "desc"
        var date = "date"
        auth = "auth"
        var url = "url"
        var act = "first"
        val extras = intent.extras
        if (extras != null) {
            user = extras.getString("title")
            desc = extras.getString("desc")
            date = extras.getString("date")
            auth = extras.getString("auth")
            act = extras.getString("activity")
            url = extras.getString("url")
        }


        Picasso.get()
                .load(url)
                .into(object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        Palette.from(bitmap!!).generate { palette ->
                            var mutedColor = palette!!.getDominantColor(android.R.attr.colorPrimary)
                            window.statusBarColor = mutedColor
                        }
                    }
                })
        val tv = findViewById<TextView>(R.id.head)
        tv1 = findViewById(R.id.author)
        tv2 = findViewById(R.id.date)
        val tv3 = findViewById<TextView>(R.id.desc)
        val iv = findViewById<ImageView>(R.id.imageView)
        tv1?.text = auth
        tv2?.text = date
        tv3.text = desc
        tv.text = user
        Picasso.get().load(url).fit().into(iv)
        val cross = findViewById<ImageView>(R.id.cross)
        val share = findViewById<ImageView>(R.id.share)
        vi = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val finalUser = user
        share.setOnClickListener { view -> OnClickShare(view, finalUser,url) }
        save = findViewById(R.id.save)
        cross.setOnClickListener { finishAfterTransition() }
        /*save?.setOnClickListener(View.OnClickListener { view ->
            runOnUiThread {
                if (ischecked) {
                    Redbms().execute()
                    vib()
                    save?.setSpeed(-3f)
                    save?.playAnimation()
                    ischecked = false
                } else {
                    ischecked = true
                    vib()
                    snack(view)
                    dbms().execute()
                    save?.setSpeed(1.5f)
                    save?.playAnimation()
                }
            }
        })*/

        save?.setOnClickListener {
            snack(save)
        }
    }



    /*inner class check : AsyncTask<Void?, Void?, Void?>() {
        var cursor: Cursor? = null
        var database: SQLiteDatabase? = null
        protected override fun doInBackground(vararg voids: Void?): Void? {
            val helper = DboffHelper(applicationContext)
            database = helper.writableDatabase
            val extras = intent.extras
            var title = ""
            if (extras != null) {
                title = extras.getString("title")
            }
            cursor = database?.query("NEWS", arrayOf("TITLE"), "TITLE LIKE ?", arrayOf(title), null, null, null)
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (cursor != null && cursor!!.moveToFirst()) {
                ischecked = true
                save!!.progress = 0.5f
                save!!.speed = 1.5f
                save!!.playAnimation()
                cursor!!.close()
                database!!.close()
            }
        }
    }*/

 /*   inner class dbms : AsyncTask<Void?, Void?, Void?>() {
        protected override fun doInBackground(vararg voids: Void?): Void? {
            val helper = DboffHelper(applicationContext)
            val database = helper.writableDatabase
            val values = ContentValues()
            var title = "NOTNOT"
            var desc = "desc"
            var date = "date"
            var auth = "auth"
            var url = "url"
            var act = "act"
            val count = 0
            val extras = intent.extras
            if (extras != null) {
                title = extras.getString("title")
                desc = extras.getString("desc")
                date = extras.getString("date")
                auth = extras.getString("auth")
                url = extras.getString("url")
                act = extras.getString("activity")
            }
            if (act == "first" && count == 0) {
                values.put("TITLE", title)
                values.put("DESCRIPTION", desc)
                values.put("DATE", date)
                values.put("AUTHOR", auth)
                values.put("IMAGEURL", url)
                database.insert("NEWS", null, values)
                database.close()
            }
            return null
        }
    }

    inner class Redbms : AsyncTask<Void?, Void?, Void?>() {
        protected override fun doInBackground(vararg voids: Void?): Void? {
            val helper = DboffHelper(applicationContext)
            val database = helper.writableDatabase
            val values = ContentValues()
            var title = "NOTNOT"
            var desc = "desc"
            var date = "date"
            var auth = "auth"
            var url = "url"
            var act = "act"
            val extras = intent.extras
            if (extras != null) {
                title = extras.getString("title")
                desc = extras.getString("desc")
                date = extras.getString("date")
                auth = extras.getString("auth")
                url = extras.getString("url")
                act = extras.getString("activity")
            }
            if (act == "first") {
                values.put("TITLE", title)
                values.put("DESCRIPTION", desc)
                values.put("DATE", date)
                values.put("AUTHOR", auth)
                values.put("IMAGEURL", url)
                database.delete("NEWS", "title=?", arrayOf(title))
                database.close()
            }
            return null
        }
    }*/

    fun vib() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vi!!.vibrate(VibrationEffect.createOneShot(30, 30))
        }
    }

    fun snack(view: View?) {
        vib()
            Snackbar.make(view!!,"This feature is disabled now", Snackbar.LENGTH_LONG)
                    .show()

    }

    @SuppressLint("SetWorldReadable")
    fun OnClickShare(view: View?, title: String?, url: String) {
        val bitmap = getBitmapFromView(idForSaveView)
        try {
            val file = File(this.externalCacheDir, "logicchip.png")
            val fOut = FileOutputStream(file)
            val uri:Uri? = Uri.parse(url)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
            fOut.flush()
            fOut.close()
            file.setReadable(true, false)
           val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            intent.putExtra(Intent.EXTRA_TEXT, title)
            intent.type = "image/png"
           startActivity(Intent.createChooser(intent, "Share image via"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBitmapFromView(view: View?): Bitmap {
        val text = "By Quizo"
        tv1!!.text = text
        tv2!!.visibility = View.INVISIBLE
        val returnedBitmap = Bitmap.createBitmap(view!!.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    override fun onResume() {
        super.onResume()
        val extras = intent.extras
        if (extras != null) {
            auth = extras.getString("auth")
        }
        tv1!!.text = auth
        tv2!!.visibility = View.VISIBLE
    }
}