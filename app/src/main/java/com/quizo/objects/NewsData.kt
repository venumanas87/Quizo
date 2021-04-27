package com.quizo.objects

class NewsData(var head: String?, var desc: String?, var date: String?, var auth: String?, var url: String?, var cont: String?, var activi: String?, var type: Int) {

    companion object {
        const val NEWS = 0
        const val STORY = 1
    }
}