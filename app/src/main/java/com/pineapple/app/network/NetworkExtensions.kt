package com.pineapple.app.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

suspend fun downloadBitmap(url: String) : Bitmap? {
    val parsedUrl = URL(url)
    val connection: HttpURLConnection?
    return try {
         withContext(Dispatchers.IO) {
            connection = parsedUrl.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
             return@withContext BitmapFactory.decodeStream(bufferedInputStream)
        }
    } catch (error: IOException) {
        error.printStackTrace()
        return null
    }
}