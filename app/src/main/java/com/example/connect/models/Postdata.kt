package com.example.connect.models

data class Postdata (val text : String ="",
                     val createdby : User=User(),
                     val createdat : Long = 0L,
                     val likedby : ArrayList<String> = ArrayList()
                     )