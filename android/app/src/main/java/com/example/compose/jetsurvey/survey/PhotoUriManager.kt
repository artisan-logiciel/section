package com.example.compose.jetsurvey.survey

import android.content.ContentValues
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore.Images.Media.*
import android.provider.MediaStore.VOLUME_EXTERNAL_PRIMARY
import java.lang.System.currentTimeMillis

/**
 * Manages the creation of photo Uris. The Uri is used to store the photos taken with camera.
 */
class PhotoUriManager(private val appContext: Context) {

    fun buildNewUri() = appContext.contentResolver.insert(
        if (SDK_INT > 28) getContentUri(VOLUME_EXTERNAL_PRIMARY)
        else EXTERNAL_CONTENT_URI,
        ContentValues().apply {
            put(DISPLAY_NAME, "selfie-${currentTimeMillis()}.jpg")
            put(MIME_TYPE, "image/jpeg")
        }
    )
}
