package com.bignerdanch.android.criminalintent

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point



// Консервативная оценка
fun getScaledBitmap(path: String, activity: Activity): Bitmap {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size)
    return getScaledBitmap(path, size.x, size.y)    // Вызов перегруженной функции - передаем destWidth и destHeight с размерами, ограниченными разрешением Activity
}


fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {    // file-level function
    // Чтение размеров изображения на диске
    var options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)

    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()

    // Выясняем, на сколько нужно уменьшить
    var inSampleSize = 1
    if (srcHeight > destHeight || srcWidth > destWidth) {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth
        val sampleScale = if (heightScale > widthScale) {
            heightScale
        } else {
            widthScale
        }
        inSampleSize = Math.round(sampleScale)
    }

    options = BitmapFactory.Options()   // новый объект позволяет гарантированно получить чистый набор настроек
    options.inSampleSize = inSampleSize

    // Чтение и создание окончательного растрового изображения
    return BitmapFactory.decodeFile(path, options)
}

