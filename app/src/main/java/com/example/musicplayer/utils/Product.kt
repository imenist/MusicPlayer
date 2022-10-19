package com.example.musicplayer.utils

import android.util.Log

data class Product(val productItem: ProductItem) : DeepCopyable<Product> {
    var productSize:ProductSize = productItem.productSize
    val pointInfo : ArrayList<ProductPoint> = productItem.pointInfo
    override fun deepCopy(): Product {
        val pi = ProductItem(productSize,pointInfo)
        Log.i("product","pi.pointInfo = productItem.pointInfo?  ${pi.pointInfo == pointInfo}")
        return Product(pi)
    }
}

interface DeepCopyable<out R> {
    fun deepCopy():R
}

