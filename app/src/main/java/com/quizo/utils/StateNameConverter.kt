package com.quizo.utils

class StateNameConverter {
    companion object{
        fun convert(code:String):String{
            return when(code){
                "AN" -> "Andaman and Nicobar Islands"
                "AP" -> "Andhra Pradesh"
                "AR" -> "Arunachal Pradesh"
                "AS" -> "Assam"
                "BR" -> "Bihar"
                "CH" -> "Chandigarh"
                "CT" -> "Chhattisgarh"
                "DL" -> "Delhi"
                "DN" -> "Dadra and Nagar Haveli and Daman and Diu"
                "GJ" -> "Gujarat"
                "GA" -> "Goa"
                "HP" -> "Himachal Pradesh"
                "HR" -> "Haryana"

                "JH" -> "Jharkhand"
                "JK" -> "Jammu and Kashmir"
                "KA" -> "Karnataka"
                "KL" -> "Kerala"
                "LA" -> "Ladakh"
                "LD" -> "Lakshadweep"
                "MH" -> "Maharashtra"
                "ML" -> "Meghalaya"
                "MN" -> "Manipur"
                "MP" -> "Madhya Pradesh"
                "NL" -> "Nagaland"
                "MZ" -> "Mizoram"
                "OR" -> "Odisha"

                "PB" -> "Punjab"
                "PY" -> "Puducherry"
                "RJ" -> "Rajasthan"
                "SK" -> "Sikkim"
                "TG" -> "Telangana"
                "TN" -> "Tamil Nadu"
                "TR" -> "Tripura"
                "UP" -> "Uttar Pradesh"

                "UT" -> "Uttarakhand"
                "WB" -> "West Bengal"
                "Andaman and Nicobar Islands" -> "AN"
                "Andhra Pradesh" -> "AP"
                "Arunachal Pradesh" -> "AR"
                "Assam" -> "AS"
                "Bihar" -> "BR"
                "Chandigarh" -> "CH"
                "Chhattisgarh" -> "CT"
                "Delhi" -> "DL"
                "Dadra and Nagar Haveli and Daman and Diu" -> "DN"
                "Gujarat" -> "GJ"
                "Goa" -> "GA"
                "Himachal Pradesh" -> "HP"
                "Haryana" -> "HR"

                "Jharkhand" -> "JH"
                "Jammu and Kashmir" -> "JK"
                "Karnataka" -> "KA"
                "Kerala" -> "KL"
                "Ladakh" -> "LA"
                "Lakshadweep" -> "LD"
                "Maharashtra" -> "MH"
                "Meghalaya" -> "MH"
                "Manipur" -> "MN"
                "Madhya Pradesh" -> "MP"
                "Nagaland" -> "NL"
                "Mizoram" -> "MZ"
                "Odisha" -> "OR"

                "Punjab" -> "PB"
                "Puducherry" -> "PY"
                "Rajasthan" -> "RJ"
                "Sikkim" -> "SK"
                "Telangana" -> "TG"
                "Tamil Nadu" -> "TN"
                "Tripura" -> "TR"
                "Uttar Pradesh" -> "UP"

                "Uttarakhand" -> "UT"
                "West Bengal" -> "WB"


                else -> ""
            }
        }
    }
}