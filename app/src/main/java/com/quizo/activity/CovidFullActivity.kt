package com.quizo.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.quizo.adapters.DataAdapter
import com.quizo.R
import com.quizo.objects.CoData
import com.quizo.utils.StateNameConverter
import com.quizo.utils.prettyCount
import com.quizo.viewmodel.CovidViewModel
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.NumberFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class CovidFullActivity:AppCompatActivity(),CoroutineScope {
    var job: Job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    var sCode:String = ""
    var fakeLoad:RelativeLayout? = null
    lateinit var cvm:CovidViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.covid_full)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
        var state:String? = null
        var dec:String? = null
        val back:ImageView = findViewById(R.id.back)
        cvm = ViewModelProvider(this).get(CovidViewModel::class.java)
        GlobalScope.launch {

            cvm.fetchData()
        }
        back.setOnClickListener {
            finishAfterTransition()
        }
        fakeLoad = findViewById(R.id.fkscrn)
        fakeLoad?.visibility = View.GONE
     val extras = intent.extras
        if (extras!=null){
            state = extras.getString("cont")
            dec = extras.getString("dec")
            ripSite(StateNameConverter.convert(state))

        }
     val decTV:TextView = findViewById(R.id.tot_dec)
        decTV.text = dec
     val toolHead:TextView = findViewById(R.id.rstate)
        toolHead.text = state?.toUpperCase(Locale.ROOT)

    }

@SuppressLint("SetTextI18n")
fun ripSite(sCode: String) {
    job = launch(Dispatchers.IO){
    drawGraphs(sCode.toLowerCase(Locale.ROOT))
    }

    runOnUiThread {
        cvm.data.observe(this, androidx.lifecycle.Observer {
            if (it.equals("E")) {
                Toast.makeText(this, "Covid Server Busy", Toast.LENGTH_SHORT).show()
            } else {
                cvm.covidData.observe(this, androidx.lifecycle.Observer {
                    for (i in it.states.indices) {
                        if (it.states[i].stateName.equals(StateNameConverter.convert(sCode))) {
                            it.states[i].let {
                                val testTV: TextView = findViewById(R.id.testcount)
                                val lastU: TextView = findViewById(R.id.lastU)
                                val totCon: TextView = findViewById(R.id.tot_conf)
                                val totRec: TextView = findViewById(R.id.tot_rec)
                                val totDec: TextView = findViewById(R.id.tot_dec)
                                val recyclerView = findViewById<RecyclerView>(R.id.recycler_view1)
                                val datalist: MutableList<CoData> = ArrayList()
                                val mAdapter = DataAdapter(datalist)
                                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
                                recyclerView.layoutManager = layoutManager
                                recyclerView.adapter = mAdapter

                                testTV.text = it.total.tested.toString()
                                lastU.text = "Last Updated on\n ${it.lastupdated.split("T")[0]}"
                                totCon.text = it.total.confirmed.toString()
                                totRec.text = it.total.recovered.toString()
                                totDec.text = it.total.deceased.toString()

                                for (j in it.districts.indices){
                                    it.districts[j].let {
                                        datalist.add(CoData("",it.name,prettyCount.pretty(it.total.deceased),prettyCount.pretty(it.total.recovered),prettyCount.pretty(it.total.confirmed),"c"))
                                        mAdapter.notifyDataSetChanged()
                                    }
                                }

                            }
                        }
                    }
                })
            }
        })
    }
}

    private fun drawGraphs(toLowerCase: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://api.covid19india.org/states_daily.json")
                .method("GET", null)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            @SuppressLint("SetTextI18n")
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val jdata = response.body!!.string()
                runOnUiThread {
                    val cnfLC: LineChart = findViewById(R.id.confirmedLC)
                    val recLC: LineChart = findViewById(R.id.recoveredLC)
                    val decLC: LineChart = findViewById(R.id.deceasedLC)
                    cnfLC.xAxis.setDrawGridLines(false)
                    cnfLC.xAxis.setDrawAxisLine(false)
                    cnfLC.xAxis.isEnabled = false
                    cnfLC.axisLeft.isEnabled = false
                    cnfLC.axisRight.isEnabled = false
                    cnfLC.setDrawBorders(false)
                    cnfLC.description.isEnabled = false
                    cnfLC.legend.isEnabled = false
                    cnfLC.animateX(3000)

                    recLC.xAxis.setDrawGridLines(false)
                    recLC.xAxis.setDrawAxisLine(false)
                    recLC.xAxis.isEnabled = false
                    recLC.axisLeft.isEnabled = false
                    recLC.axisRight.isEnabled = false
                    recLC.setDrawBorders(false)
                    recLC.description.isEnabled = false
                    recLC.legend.isEnabled = false
                    recLC.animateX(4000)

                    decLC.xAxis.setDrawGridLines(false)
                    decLC.xAxis.setDrawAxisLine(false)
                    decLC.xAxis.isEnabled = false
                    decLC.axisLeft.isEnabled = false
                    decLC.axisRight.isEnabled = false
                    decLC.setDrawBorders(false)
                    decLC.description.isEnabled = false
                    decLC.legend.isEnabled = false
                    decLC.animateX(5000)

                    val listData1 = ArrayList<Entry>()
                    val listData2 = ArrayList<Entry>()
                    val listData3 = ArrayList<Entry>()
                    try {
                      val mainObj = JSONObject(jdata)
                       val mainArr = mainObj.getJSONArray("states_daily")
                       var cnfC = arrayListOf<Float>()
                       val recC = arrayListOf<Float>()
                        val decC = arrayListOf<Float>()
                        val masize = mainArr.length()
                        var c = 0
                        var r = 0
                        var d = 0
                        Log.d(TAG, "onResponse: $masize")
                        for (i in 0 until masize){
                           val childObjs = mainArr.getJSONObject(i)

                            if (childObjs.getString("status")=="Confirmed"){
                                c++
                                val count = childObjs.getInt(toLowerCase).toFloat()
                              cnfC.add(count)
                            }
                            if (childObjs.getString("status")=="Recovered"){
                                r++
                                val count = childObjs.getInt(toLowerCase).toFloat()
                                recC.add(count)
                            }
                            if (childObjs.getString("status")=="Deceased"){
                                d++
                                val count = childObjs.getInt(toLowerCase).toFloat()
                                decC.add(count)
                            }


                        }
                        for (j in 0 until c){
                            listData1.add(Entry(j.toFloat(),cnfC[j]))
                        }
                        for (j in 0 until r){
                            listData2.add(Entry(j.toFloat(),recC[j]))
                        }
                        for (j in 0 until d){
                            listData3.add(Entry(j.toFloat(),decC[j]))
                        }
                        val lineDataSet1 = LineDataSet(listData1, "CHART")
                        lineDataSet1.color = ContextCompat.getColor(applicationContext, R.color.redChart)
                        lineDataSet1.disableDashedLine()
                        lineDataSet1.setDrawValues(false)
                        lineDataSet1.lineWidth = 1.5f
                        lineDataSet1.mode = LineDataSet.Mode.CUBIC_BEZIER
                        lineDataSet1.valueTextColor = ContextCompat.getColor(applicationContext, android.R.color.white)
                        lineDataSet1.setDrawCircles(false)
                        val lineData1 = LineData(lineDataSet1)
                        cnfLC.data = lineData1
                        cnfLC.notifyDataSetChanged()
                        cnfLC.invalidate()

                        val lineDataSet2 = LineDataSet(listData2, "CHART")
                        lineDataSet2.color = ContextCompat.getColor(applicationContext, R.color.recChart)
                        lineDataSet2.disableDashedLine()
                        lineDataSet2.setDrawValues(false)
                        lineDataSet2.lineWidth = 1.5f
                        lineDataSet2.mode = LineDataSet.Mode.CUBIC_BEZIER
                        lineDataSet2.valueTextColor = ContextCompat.getColor(applicationContext, android.R.color.white)
                        lineDataSet2.setDrawCircles(false)
                        val lineData2 = LineData(lineDataSet2)
                        recLC.data = lineData2
                        recLC.notifyDataSetChanged()
                        recLC.invalidate()

                        val lineDataSet3 = LineDataSet(listData3, "CHART")
                        lineDataSet3.color = ContextCompat.getColor(applicationContext, R.color.gray)
                        lineDataSet3.disableDashedLine()
                        lineDataSet3.setDrawValues(false)
                        lineDataSet3.lineWidth = 1.5f
                        lineDataSet3.mode = LineDataSet.Mode.CUBIC_BEZIER
                        lineDataSet3.valueTextColor = ContextCompat.getColor(applicationContext, android.R.color.white)
                        lineDataSet3.setDrawCircles(false)
                        val lineData3 = LineData(lineDataSet3)
                        decLC.data = lineData3
                        decLC.notifyDataSetChanged()
                        decLC.invalidate()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            }
      })
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    companion object {
        const val TAG = "COVIDFULL"
    }
}


