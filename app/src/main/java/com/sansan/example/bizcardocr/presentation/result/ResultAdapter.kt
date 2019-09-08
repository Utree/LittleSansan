package com.sansan.example.bizcardocr.presentation.result

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sansan.example.bizcardocr.R
import com.sansan.example.bizcardocr.domain.model.BizCardInfo
import com.sansan.example.bizcardocr.domain.model.OcrState
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_result_element.*
import kotlinx.android.synthetic.main.item_result_header.*

class ResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ElementType(@StringRes val resId: Int) {
        PERSON_NAME(R.string.result_element_title_person_name),
        EMAIL(R.string.result_element_title_email),
        TEL(R.string.result_element_title_tel),
        COMPANY_NAME(R.string.result_element_title_company_name)
    }

    interface Item {
        val viewType: Int
    }

    private val items = mutableListOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType) {
        VIEW_TYPE_HEADER -> HeaderItemViewHolder(parent)
        VIEW_TYPE_ELEMENT -> ElementItemViewHolder(parent)
        else -> throw IllegalArgumentException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when(item.viewType) {
            VIEW_TYPE_HEADER -> {
                val headerItem = (item as HeaderItem)
                (holder as HeaderItemViewHolder).bind(headerItem.bizCardImage, headerItem.state)
            } VIEW_TYPE_ELEMENT -> {
                val elementItem =  (item as ElementItem)
                (holder as ElementItemViewHolder).bind(elementItem.type.resId, elementItem.value)
            }
        }
    }

    override fun getItemCount(): Int = items.size
    override fun getItemViewType(position: Int) = items[position].viewType

    fun loading(bizCardImage: Bitmap) {
        HeaderItem(bizCardImage, OcrState.PROCESSING).let(items::add)
        ElementItem(ElementType.PERSON_NAME, null).let(items::add)
        ElementItem(ElementType.EMAIL, null).let(items::add)
        ElementItem(ElementType.TEL, null).let(items::add)
        ElementItem(ElementType.COMPANY_NAME, null).let(items::add)
        notifyDataSetChanged()
    }

    fun updateOcrFailed(ocrState: OcrState) {
        val herderItem = items.first { it.viewType == VIEW_TYPE_HEADER } as HeaderItem
        herderItem.state = ocrState
        notifyItemChanged(0)
    }

    fun updateOcrSuccess(ocrResult: BizCardInfo) {
        items.map {
            when(it.viewType) {
                VIEW_TYPE_HEADER -> (it as HeaderItem).state = OcrState.SUCCESS
                VIEW_TYPE_ELEMENT -> updateOcrResult(ocrResult, (it as ElementItem))
            }
        }
        notifyDataSetChanged()
    }

    private fun updateOcrResult(ocrResult: BizCardInfo, elementItem: ElementItem) {

        when(elementItem.type) {
            ElementType.PERSON_NAME -> elementItem.value = ocrResult.personName
            ElementType.EMAIL -> elementItem.value = ocrResult.email
            ElementType.TEL -> elementItem.value = ocrResult.tel
            ElementType.COMPANY_NAME -> elementItem.value = ocrResult.companyName
        }
    }

    private class HeaderItem(
            val bizCardImage: Bitmap,
            var state: OcrState): Item {
        override  val viewType = VIEW_TYPE_HEADER
    }

    private class ElementItem(
            val type:ElementType,
            var value: String?): Item {
        override  val viewType = VIEW_TYPE_ELEMENT
    }

    private class HeaderItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_result_header, parent, false)
    ), LayoutContainer {

        override val containerView: View? get() = itemView

        fun bind(image: Bitmap, state: OcrState) {
            bizCardImage.setImageBitmap(image)
            when(state) {
                OcrState.PROCESSING -> R.string.result_header_title_processing
                OcrState.SUCCESS -> R.string.result_header_title_success
                OcrState.NETWORK_FAILURE, OcrState.REQUEST_FAILURE -> R.string.result_header_title_failure
            }.let(ocrStateDescription::setText)
        }
    }

    private class ElementItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_result_element, parent, false)
    ), LayoutContainer {

        override val containerView: View? get() = itemView

        fun bind(@StringRes title: Int, value: String?) {
            labelText.setText(title)
            value?.let{
                valueText.text = it
                valueText.setTextColor(Color.BLACK)
            } ?: run {
                valueText.text = "未設定"
                valueText.setTextColor(Color.RED)
            }
        }
    }

    private companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_ELEMENT = 2
    }
}
