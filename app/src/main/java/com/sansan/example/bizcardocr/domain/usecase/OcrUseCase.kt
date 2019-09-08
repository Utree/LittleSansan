package com.sansan.example.bizcardocr.domain.usecase

import com.sansan.example.bizcardocr.data.repository.OcrRepository
import com.sansan.example.bizcardocr.domain.entity.googleapis.vision.ImagesResponse
import com.sansan.example.bizcardocr.domain.model.BizCardInfo

object OcrUseCase {
    sealed class OcrResult {
        class Success(val bizCardInfo: BizCardInfo): OcrResult()
        object RequestError: OcrResult()
        object NetworkError: OcrResult()
    }

    suspend fun bizCardOcr(rawImage: ByteArray): OcrResult {
        try {
            OcrRepository.ocrRequest(rawImage)?.let(this::parseResponse)?.let {
                return OcrResult.Success(it)
            } ?: return  OcrResult.RequestError
        } catch (t: Throwable) {
            println(t)
            return OcrResult.NetworkError
        }
    }

    private fun parseResponse(response: ImagesResponse): BizCardInfo? =
            BizCardParseUseCase.parseOcrResult(response)
}