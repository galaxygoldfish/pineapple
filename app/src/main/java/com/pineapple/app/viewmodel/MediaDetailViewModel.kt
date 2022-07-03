package com.pineapple.app.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pineapple.app.network.GfycatNetworkService
import com.pineapple.app.network.NetworkServiceBuilder.GFYCAT_BASE_URL
import com.pineapple.app.network.NetworkServiceBuilder.apiService
import com.pineapple.app.network.downloadBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URLDecoder

class MediaDetailViewModel : ViewModel() {


    var url by mutableStateOf<String?>(null)
    val gfycatService by lazy { apiService<GfycatNetworkService>(GFYCAT_BASE_URL) }

    suspend fun downloadImage(url: String, context: Context) {
        val bitmap = downloadBitmap(url)
        createImagePlaceholder(context)?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun createImagePlaceholder(context: Context): OutputStream? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver?.let { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageURI = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                return@let imageURI?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, "${System.currentTimeMillis()}.jpg")
            return withContext(Dispatchers.IO) { FileOutputStream(image) }
        }
    }
}