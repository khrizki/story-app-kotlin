package com.dicoding.picodiploma.loginwithanimation.view.addStory

import android.content.*
import android.graphics.*
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.dicoding.picodiploma.loginwithanimation.BuildConfig
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val MAX_FILE_SIZE = 1000000
private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
private val timestamp: String = SimpleDateFormat(DATE_FORMAT, Locale.US).format(Date())

fun convertUriToFile(uri: Uri, context: Context): File {
    val tempFile = createTempImageFile(context)
    val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return tempFile
    val outputStream = FileOutputStream(tempFile)
    val buffer = ByteArray(1024)
    var bytesRead: Int
    while (inputStream.read(buffer).also { bytesRead = it } > 0) {
        outputStream.write(buffer, 0, bytesRead)
    }
    outputStream.close()
    inputStream.close()
    return tempFile
}

fun File.compressImage(): File {
    val originalBitmap = BitmapFactory.decodeFile(this.path)
    var quality = 100
    var fileLength: Int
    do {
        val byteArrayStream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayStream)
        val byteArray = byteArrayStream.toByteArray()
        fileLength = byteArray.size
        quality -= 5
    } while (fileLength > MAX_FILE_SIZE)
    originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(this))
    return this
}

fun createTempImageFile(context: Context): File {
    val tempDir = context.externalCacheDir
    return File.createTempFile(timestamp, ".jpg", tempDir)
}

fun generateImageUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timestamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: getLegacyImageUri(context)
    } else {
        getLegacyImageUri(context)
    }
}

private fun getLegacyImageUri(context: Context): Uri {
    val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(picturesDir, "/MyCamera/$timestamp.jpg")
    imageFile.parentFile?.takeIf { !it.exists() }?.mkdir()
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )
}
