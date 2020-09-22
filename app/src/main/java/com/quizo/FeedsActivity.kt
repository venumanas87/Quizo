package com.quizo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.card.MaterialCardView
import com.jaeger.library.StatusBarUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okio.IOException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import ru.bullyboo.text_animation.TextCounter
import kotlin.coroutines.CoroutineContext

class FeedsActivity:AppCompatActivity(),CoroutineScope {
    var population:TextView? = null
    var deaths:TextView? = null
    var birth:TextView? = null
    var migra:TextView? = null
    var lastU:TextView? = null
    var job: Job = Job()
    var bottomNavigationView:BottomNavigationView? = null
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feeds_layout)
        StatusBarUtil.setTransparent(this)

        population = findViewById(R.id.popcnt)
        birth = findViewById(R.id.yrch)
        deaths = findViewById(R.id.gr)
        migra = findViewById(R.id.gs)
        lastU = findViewById(R.id.lastU)
        val c1:MaterialCardView = findViewById(R.id.c1)
        val c2:MaterialCardView = findViewById(R.id.c2)
        val c3:MaterialCardView = findViewById(R.id.c3)
        val c4:MaterialCardView = findViewById(R.id.c4)
        c1.setOnClickListener {
            startActivity(Intent(this,NewsActivity1::class.java))
        }
        c2.setOnClickListener {
            startActivity(Intent(this,EditorialActivity::class.java))
        }
        c3.setOnClickListener {
            startActivity(Intent(this,StoryFullActivity::class.java))
        }
        c4.setOnClickListener {
            startActivity(Intent(this,ArticleActivity::class.java))
        }
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.btmnav)
        bottomNavigationView?.selectedItemId = R.id.page_2
        bottomNavigationView?.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
        bottomNavigationView?.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_2 -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.page_1 -> {
                    startActivity(Intent(applicationContext, LoggedIn::class.java))
                    overridePendingTransition(0, 0)
                }

                R.id.page_4 -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnNavigationItemSelectedListener true

                }

            }
            false
        })

       job = launch(Dispatchers.IO){ demographics()}
    }

    fun demographics(){
        var doc:Document
        try {
            doc = Jsoup.connect("https://countrymeters.info/en/India").get()
            var popcnt:Element = doc.select("div.data_div").first().selectFirst("div[id='cp1']")
            var death:Element = doc.select("div.data_div").first().selectFirst("div[id='cp7']")
            var births:Element = doc.select("div.data_div").first().selectFirst("div[id='cp9']")
            var mig:Element = doc.select("div.data_div").first().selectFirst("div[id='cp11']")
            var pop = popcnt.text().filter { it.isDigit() }
            var dea = death.text().filter { it.isDigit() }
            var bir = births.text().filter { it.isDigit() }
            var mi = mig.text().filter { it.isDigit() }
            Log.d("TAG", "demographics: $pop")

            runOnUiThread {
                lastU?.text = "Last Updated : Just Now"
                TextCounter.newBuilder()
                        .setTextView(population!!)
                        .setType(TextCounter.LONG)
                        .from(1394930000)
                        .to(pop)
                        .setFPS(60)
                        .setDuration(2000)
                        .build()
                        .start()

                TextCounter.newBuilder()
                        .setTextView(birth!!)
                        .setPrefix("Births Today\n\n")
                        .setType(TextCounter.LONG)
                        .from(0)
                        .to(bir)
                        .setFPS(60)
                        .setDuration(3000)
                        .build()
                        .start()

            TextCounter.newBuilder()
                    .setTextView(deaths!!)
                    .setPrefix("Deaths Today\n\n")
                    .setType(TextCounter.LONG)
                    .from(0)
                    .to(dea)
                    .setFPS(60)
                    .setDuration(3000)
                    .build()
                    .start()
                TextCounter.newBuilder()
                        .setTextView(migra!!)
                        .setPrefix("Migrated Today\n \n -")
                        .setType(TextCounter.LONG)
                        .from(0)
                        .to(mi)
                        .setFPS(60)
                        .setDuration(3000)
                        .build()
                        .start()
        }

        }catch (e:IOException){

        }
    }


    override fun onPostResume() {
        super.onPostResume()
        bottomNavigationView?.selectedItemId = R.id.page_2
    }
}