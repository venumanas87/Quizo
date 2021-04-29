package com.quizo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quizo.objects.CovidData
import com.quizo.objects.Delta
import com.quizo.objects.Districts
import com.quizo.objects.States
import com.quizo.utils.StateNameConverter
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CovidViewModel: ViewModel() {
    val data:MutableLiveData<String> = MutableLiveData()
    val covidData:MutableLiveData<CovidData> = MutableLiveData()

    fun fetchData(){
        val client = OkHttpClient()
        val request = Request.Builder()
        //https://api.covid19india.org/v4/min/data.min.json
                .url("https://api.covid19india.org/v4/min/data.min.json")
                .method("GET", null)
                .build()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                data.postValue("E")
            }

            override fun onResponse(call: Call, response: Response) {
                val d = response.body!!.string()
                println("response $d")
                data.postValue(d)
                try {
                    val jobj = JSONObject(d)
                    val statesArray: JSONArray = jobj.names()
                    val statesList: ArrayList<States> = ArrayList()
                    for (i in 0 until statesArray.length()) {
                        var districtsList: ArrayList<Districts> = ArrayList()
                        var totalS: Delta? = null
                        var deltaS: Delta? = null
                        val name = StateNameConverter.convert(statesArray.getString(i))
                        val stateObj = jobj.getJSONObject(statesArray.getString(i))
                        val stateobjArr = stateObj.names()
                        for (k in 0 until stateobjArr.length()){
                            if(stateobjArr.getString(k) == "delta7"){
                                val deltaObj = stateObj.getJSONObject("delta7")
                                val deltaobjArr = deltaObj.names()
                                var confirmed = 0
                                var deceased = 0
                                var recovered = 0
                                var tested = 0
                                for (l in 0 until deltaobjArr.length()){
                                    when(deltaobjArr.getString(l)){
                                        "confirmed" ->{
                                            confirmed = deltaObj.getInt("confirmed")
                                        }
                                        "recovered"->{
                                            recovered = deltaObj.getInt("recovered")
                                        }
                                        "deceased" ->{
                                            deceased = deltaObj.getInt("deceased")
                                        }
                                        "tested" ->{
                                            tested = deltaObj.getInt("tested")
                                        }
                                    }
                                }
                                deltaS = Delta(confirmed,deceased, recovered, tested, 0)
                            }

                            if (stateobjArr.getString(k) == "total"){
                                val totalObj = stateObj.getJSONObject("total")
                                val totalobjArr = totalObj.names()
                                var confirmed = 0
                                var deceased = 0
                                var recovered = 0
                                var tested = 0
                                for (l in 0 until totalobjArr.length()){
                                    when(totalobjArr.getString(l)){
                                        "confirmed" ->{
                                            confirmed = totalObj.getInt("confirmed")
                                        }
                                        "recovered"->{
                                            recovered = totalObj.getInt("recovered")
                                        }
                                        "deceased" ->{
                                            deceased = totalObj.getInt("deceased")
                                        }
                                        "tested" ->{
                                            tested = totalObj.getInt("tested")
                                        }
                                    }
                                }
                                totalS = Delta(confirmed,deceased, recovered, tested, 0)

                            }
                        }


                        val last_updated = stateObj.getJSONObject("meta").getString("last_updated")
                        statesList.add(States(name, deltaS?: Delta(0,0,0,0,0), districtsList, last_updated, totalS?: Delta(0,0,0,0,0)))
                        if(name.isNotEmpty()){
                            val districtObj = stateObj.getJSONObject("districts")
                            val districtsArr = districtObj.names()
                            for (j in 0 until districtsArr.length()) {
                                val dname = districtsArr.getString(j)
                                println("venud $dname")
                                val distObj = districtObj.getJSONObject(dname)
                                var totalD: Delta? = null
                                var deltaD: Delta? = null
                                val distobjArr = distObj.names()
                                for (m in 0 until distobjArr.length()){
                                    if(distobjArr.getString(m) == "delta7"){
                                        val deltaObj = distObj.getJSONObject("delta7")
                                        val deltaobjArr = deltaObj.names()
                                        var confirmed = 0
                                        var deceased = 0
                                        var recovered = 0
                                        var tested = 0
                                        for (l in 0 until deltaobjArr.length()){
                                            when(deltaobjArr.getString(l)){
                                                "confirmed" ->{
                                                    confirmed = deltaObj.getInt("confirmed")
                                                }
                                                "recovered"->{
                                                    recovered = deltaObj.getInt("recovered")
                                                }
                                                "deceased" ->{
                                                    deceased = deltaObj.getInt("deceased")
                                                }
                                                "tested" ->{
                                                    tested = deltaObj.getInt("tested")
                                                }
                                            }
                                        }
                                        deltaD = Delta(confirmed,deceased, recovered, tested, 0)
                                    }

                                    if (distobjArr.getString(m) == "total"){
                                        val totalObj = distObj.getJSONObject("total")
                                        val totalobjArr = totalObj.names()
                                        var confirmed = 0
                                        var deceased = 0
                                        var recovered = 0
                                        var tested = 0
                                        for (l in 0 until totalobjArr.length()){
                                            when(totalobjArr.getString(l)){
                                                "confirmed" ->{
                                                    confirmed = totalObj.getInt("confirmed")
                                                }
                                                "recovered"->{
                                                    recovered = totalObj.getInt("recovered")
                                                }
                                                "deceased" ->{
                                                    deceased = totalObj.getInt("deceased")
                                                }
                                                "tested" ->{
                                                    tested = totalObj.getInt("tested")
                                                }
                                            }
                                        }
                                        totalD = Delta(confirmed,deceased, recovered, tested, 0)

                                    }
                                }
                                districtsList.add(Districts(dname, deltaD?: Delta(0,0,0,0,0), totalD?: Delta(0,0,0,0,0)))
                            }
                        }


                    }

                    val cvdData = CovidData(statesList)
                    covidData.postValue(cvdData)


                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            }

        })
    }
}