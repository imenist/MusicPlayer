package com.example.musicplayer.utils
import com.example.musicplayer.utils.setLast as SL

fun main(){
    var sb:StringBuilder = java.lang.StringBuilder("abc").SL("SLlast")
    println(sb)
    KotlinTest1().printa()
}

class KotlinTest1{
    var a:Int ?= 0

    var StringBuilder.lastChar : Char
        get(){
            return get(lastIndex)
        }
        set(value:Char) {
            this.setCharAt(lastIndex,value)
        }
    fun Int.goodInt(good:Int = 20,bad:Int = 10):Int{
        return this * (good + bad)
    }
    fun printa(){
        println(a!!.goodInt())
        var b:StringBuilder = java.lang.StringBuilder("good!")
        b.lastChar = '?'
        println(b)
    }
}