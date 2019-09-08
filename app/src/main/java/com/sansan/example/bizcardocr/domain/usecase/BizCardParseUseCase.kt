package com.sansan.example.bizcardocr.domain.usecase

import com.sansan.example.bizcardocr.domain.entity.googleapis.vision.EntityAnnotation
import com.sansan.example.bizcardocr.domain.entity.googleapis.vision.ImagesResponse
import com.sansan.example.bizcardocr.domain.model.BizCardInfo

// FIXME: 名刺や表示項目に合わせて下記内容は適宜見直してください。
object BizCardParseUseCase {
    private val regexFindCompany = """株式会社""".toRegex()
    // TODO: 課題3 (名刺中のEmail、電話番号(TEL)情報を取り出すための正規表現パターンを作成してください)
    // BizCardOcrUseCaseTestの3つのテストが通るようなパターンを実装してください。
    private val regexEmail = """E-mail ([a-zA-Z0-9.!#${'$'}%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9-.]+[a-zA-Z0-9-])""".toRegex()
    private val regexTel = """TEL (\d+-\d+-\d+)""".toRegex()
    private val regexFindName = """^\p{InCJKUnifiedIdeographs}{1,3}\s?\p{InCJKUnifiedIdeographs}{1,3}$""".toRegex()
    private val regexMatchDepartmentPosition = """[部課任(会計|部長)]$""".toRegex()

    fun parseOcrResult(response: ImagesResponse): BizCardInfo? {
        if (response.responses.isEmpty()) {
            return null
        }

        // 1番目のOCR結果のみ使用する
        val result = response.responses.first()
        return result.textAnnotations.let(this::parseTextAnnotation)
    }

    private fun parseTextAnnotation(textAnnotation: List<EntityAnnotation>): BizCardInfo {
        var personName: String? = null
        var companyName: String? = null
        var email: String? = null
        var tel: String? = null

        val allTexts = textAnnotation.first().description.split("\n")

        allTexts.map {
            when {
                isCompanyName(it) -> companyName = getCompanyName(it)
                isEmail(it) -> email = getEmail(it)
                isTel(it) -> tel = getTel(it)
                isPersonName(it) -> personName = getPersonName(it)
            }
        }
        return BizCardInfo(personName, email, tel, companyName)
    }

    private fun isCompanyName(text: String): Boolean =
            regexFindCompany.containsMatchIn(text)

    // 1行まるまる会社名が来る想定(名刺の種類に応じて適宜見直す)
    private fun getCompanyName(text: String): String? = text

    private fun isEmail(text: String): Boolean =
            regexEmail.containsMatchIn(text)

    private fun getEmail(text: String): String? =
            regexEmail.find(text)?.groupValues?.getOrNull(1)

    private fun isTel(text: String): Boolean =
            regexTel.containsMatchIn(text)

    private fun getTel(text: String): String? =
            regexTel.find(text)?.groupValues?.getOrNull(1)

    private fun isPersonName(text: String): Boolean =
            regexFindName.containsMatchIn(text) && !regexMatchDepartmentPosition.containsMatchIn(text)

    // 1行まるまる氏名が来る想定(名刺の種類に応じて適宜見直す)
    private fun getPersonName(text: String): String? = text
}
