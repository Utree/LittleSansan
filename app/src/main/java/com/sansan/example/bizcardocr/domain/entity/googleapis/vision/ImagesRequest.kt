package com.sansan.example.bizcardocr.domain.entity.googleapis.vision


// TODO: 課題2 (要求パラメータを実装してください)
// APIドキュメント(https://cloud.google.com/vision/docs/reference/rest/v1/images/annotate#AnnotateImageRequest)を参考に
// 要求パラメータの実装を完成させてください。
//
// 本アプリでOCRするために最低限必要なパラメータは次のとおりです(その他のパラメータは必要に応じて追加してみてください)。
// 1. Imageモデル (content) ※ 画像をbase64エンコーディングした文字列を指定してください
// 2. Featureモデル(type, maxResults) ※ 10を設定すると良いでしょう
data class ImagesRequest(
        val requests: List<AnnotateImageRequest>
) {
    companion object {
        // TODO: 課題2 (要求パラメータに合わせて引数を修正してください)
        fun createSingleRequest(base64Image: String): ImagesRequest{
            val item = AnnotateImageRequest(Image(base64Image),
                    Feature(Type.TEXT_DETECTION, 10),
                    ImageContext(listOf(LanguageHint.JAPANESE.language, LanguageHint.ENGLISH.language)))
            val requests: List<AnnotateImageRequest> = listOf(item)
            return ImagesRequest(requests)
        }
    }
}

data class AnnotateImageRequest(
        val image: Image,
        val features: Feature,
        val imageContext: ImageContext
)

data class Image(
        val content: String
)

data class Feature(
        val type: Type,
        val maxResults: Int
)

enum class Type(val type: String) {
    TEXT_DETECTION("TEXT_DETECTION")
}

data class ImageContext(
        val languageHints: List<String>
)

enum class LanguageHint(val language: String) {
    JAPANESE("ja"),
    ENGLISH("en")
}
