package com.sansan.example.bizcardocr.data.repository

import android.util.Base64
import com.sansan.example.bizcardocr.data.network.ApiProvider
import com.sansan.example.bizcardocr.domain.entity.googleapis.vision.ImagesRequest
import com.sansan.example.bizcardocr.domain.entity.googleapis.vision.ImagesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object OcrRepository {

    suspend fun ocrRequest(rawImage: ByteArray): ImagesResponse? {
        // TODO: 課題2 (rawImageオブジェクトをBase64文字列へ変換してください)
        val base64Image = Base64.encodeToString(rawImage, Base64.NO_WRAP)

        val request = ImagesRequest.createSingleRequest(base64Image)
        val response = withContext(Dispatchers.Default) { ApiProvider.visionApi.images(request).execute() }
        return when (response.code()) {
            200 -> response.body()
            else -> null
        }
    }
}