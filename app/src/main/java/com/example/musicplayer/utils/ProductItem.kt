package com.example.musicplayer.utils

data class ProductItem(
    val productSize : ProductSize = ProductSize(),
    val pointInfo : ArrayList<ProductPoint> = arrayListOf()
) {
}


data class ProductSize(var height:Float = 1920f,var width:Float = 1080f)

data class ProductPoint(var x:Int,var y:Int)