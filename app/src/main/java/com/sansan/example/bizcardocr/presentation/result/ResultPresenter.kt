package com.sansan.example.bizcardocr.presentation.result

import android.content.Intent
import com.sansan.example.bizcardocr.domain.usecase.OcrUseCase
import com.sansan.example.bizcardocr.utility.CancelableCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultPresenter(
        private val resultView: ResultContract.View,
        private val coroutineScope: CancelableCoroutineScope
): ResultContract.Presenter {

    override fun onStart(rawImage: ByteArray) {
        request(rawImage)
    }

    private fun request(rawImage: ByteArray) {
        coroutineScope.launch(Dispatchers.Default) {
            val ocrResult = OcrUseCase.bizCardOcr(rawImage)
            coroutineScope.launch(Dispatchers.Main) {
                // TODO: 課題3 (ResultContract.Viewで定義したメソッドを呼び出し、OCR結果をActivityへ通知してください)
                // ・OCR成功時(is OcrUseCase.OcrResult.Success)の際にはocrResult.bizCardInfoをActivityへ通知してください。
                // ・OCR失敗時はOCRが失敗したことをActivityへ通知してください。
                when(ocrResult) {
                    is OcrUseCase.OcrResult.Success -> resultView.ocrIsSuccess(ocrResult.bizCardInfo)
                    is OcrUseCase.OcrResult.RequestError -> resultView.ocrIsFailed("Request Error")
                    is OcrUseCase.OcrResult.NetworkError -> resultView.ocrIsFailed("Network Error")
                }
            }
        }
    }
}
