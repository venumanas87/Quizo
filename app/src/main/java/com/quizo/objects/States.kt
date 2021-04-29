package com.quizo.objects

data class States(
        var stateName:String,
        var delta:Delta,
        var districts:List<Districts>,
        var lastupdated:String,
        var total:Delta
)
