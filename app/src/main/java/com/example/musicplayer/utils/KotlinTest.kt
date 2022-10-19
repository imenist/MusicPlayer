package com.example.musicplayer.utils


fun main(){
    val c = "C"
    var str:StringBuilder = java.lang.StringBuilder("abc").setLast("last")
    println(str)
    println(KotlinTest1::printa)
    str.lastChar = '!'
    println(str)
}

fun StringBuilder.setLast(last:String):StringBuilder{
    replace(lastIndex,length,last)
    return this
}

var StringBuilder.lastChar : Char
    get(){
        return get(lastIndex)
    }
    set(value:Char) {
        this.setCharAt(lastIndex,value)
    }
