package com.quizo

import android.R.attr
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.*
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.story_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class StoryView : AppCompatActivity(),CoroutineScope {
    var bgblur: ImageView? = null
    var imgcon: ImageView? = null
    var ischecked = false
    var save:LottieAnimationView?=null
    var vib: Vibrator? = null
    private var jobb: Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + jobb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_view)
        var toolbar: Toolbar? = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar?.setTitle(" ")
        var collapse:CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        var title: TextView? = findViewById(R.id.stit)
        var cont: TextView? = findViewById(R.id.content)
        bgblur = findViewById(R.id.bgblur)
        imgcon = findViewById(R.id.imgm)
        var back:ImageView = findViewById(R.id.back)
        val mAdView: AdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        back.setOnClickListener {
            supportFinishAfterTransition();
        }
        var auth = findViewById<TextView>(R.id.author)
        save = findViewById(R.id.savee)
        var chip:Chip = findViewById(R.id.gchip)
        var titles: String? = null
        var imgurl: String? = null
        var con: String? = null
        var author: String? = null
        var date: String? = null
        val extras = intent.extras

        if (extras != null) {
            titles = extras.getString("title")
            imgurl = extras.getString("url")
            con = extras.getString("desc")
            author = extras.getString("auth")
            date = extras.getString("date")
        }
        Picasso.get()
                .load(imgurl)
                .into(object :Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        Palette.from(bitmap!!).generate { palette ->
                            var mutedColor = palette!!.getDominantColor(attr.colorPrimary)
                            collapse.setContentScrimColor(mutedColor)
                            collapse.setBackgroundColor(mutedColor)
                            window.statusBarColor = mutedColor
                        }
                    }
                })

        appbar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            bgblur?.setAlpha(1.0f - Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat()))
            imgcon?.setAlpha(1.0f - Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat()))
        })




        auth?.setText(author)
        title?.setText(titles)
        cont?.setText(con)
        Picasso.get().load(imgurl).transform(BlurTransformation(applicationContext)).fit().into(bgblur)
        Picasso.get().load(imgurl).fit().into(imgcon)
        //Picasso.get().load(imgurl).fit().into(imgcon)
       /* var uri:Uri = Uri.parse(imgurl)*/
        //val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.fromFile(File(imgurl)))
    save?.setOnClickListener {
        if (ischecked) {
            jobb = launch(Dispatchers.IO){Redbms()}
           // vib()
            save?.setSpeed(-3f)
            save?.playAnimation()
            ischecked = false
        } else {
            ischecked = true
           vib()
            snack(save)
            jobb = launch(Dispatchers.IO){dbms()}
            save?.setSpeed(1.5f)
            save?.playAnimation()
        }
    }

        jobb = launch(Dispatchers.IO){
            check()
        }

    }

    //////////////////////////////////////////////////DBMS FUNCTIONS////////////////////////////////////////////////////////


   private fun check() {
       var cursor: Cursor?
       var database: SQLiteDatabase? = null
       val helper = DboffHelper(applicationContext)
       database = helper.writableDatabase
       val extras = intent.extras
       var title = ""
       if (extras != null) {
           title = extras.getString("title")
       }
       cursor = database?.query("STORY", arrayOf("TITLE"), "TITLE LIKE ?", arrayOf(title), null, null, null)

       runOnUiThread {
           if (cursor != null && cursor!!.moveToFirst()) {
               ischecked = true
               save!!.progress = 0.5f
               save!!.speed = 1.5f
               save!!.playAnimation()
               cursor!!.close()
               database!!.close()
           }

       }
   }

    private fun dbms() {
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
            database.insert("STORY", null, values)
            database.close()
        }
    }

    private fun Redbms() {
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
            database.delete("STORY", "title=?", arrayOf(title))
            database.close()
        }
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   private fun vib() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib?.vibrate(VibrationEffect.createOneShot(30, 30))
        }
    }

   private  fun snack(view: View?) {
        var act = "act"
        val extras = intent.extras
        if (extras != null) {
            act = extras.getString("activity")
        }
        if (act == "first") {
            Snackbar.make(view!!, R.string.saved, Snackbar.LENGTH_LONG)
                    .setAction("show") { startActivity(Intent(applicationContext, OffActivity::class.java)) }
                    .show()
        } else {
            Snackbar.make(view!!, R.string.saved, Snackbar.LENGTH_LONG)
                    .show()
        }
    }


}


