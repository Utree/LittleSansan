package com.sansan.example.bizcardocr.presentation.result

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.sansan.example.bizcardocr.BizCardOCRApplication
import com.sansan.example.bizcardocr.R
import com.sansan.example.bizcardocr.domain.model.BizCardInfo
import com.sansan.example.bizcardocr.utility.CancelableCoroutineScope
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity(), ResultContract.View {
    private val coroutineScope = CancelableCoroutineScope()
    private val presenter: ResultContract.Presenter by lazy { ResultPresenter(this, coroutineScope) }
    private val adapter: ResultAdapter = ResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val bizCardOcrApplication = application as BizCardOCRApplication
        val rawBitmap = bizCardOcrApplication.tempRawPicture!!
        presenter.onStart(rawBitmap)
        initBizCardInfoView(rawBitmap)
        initToolBar()
    }

    // TODO: 課題3 (ResultPresenterからOCR成功通知を受け取り、RecyclerViewへの反映・Toast表示を実装してください)
    // ResultContract.Viewにて定義したInterfaceを実装し、OCR成功時と失敗時とで次のような処理になるように実装してください。
    // ・OCR成功時
    //   ① ResultAdapterへOCR結果を渡して表示
    // ・OCR失敗時
    //   ① OCRに失敗した旨をToast表示
    //   ② ResultAdapterへ失敗したことを通知し、OCRに失敗した旨を表示

    override fun ocrIsFailed(reason: String) {
        Toast.makeText(this, "ocrに失敗しました", Toast.LENGTH_LONG).show()
    }

    override fun ocrIsSuccess(data: BizCardInfo) {
        adapter.updateOcrSuccess(data!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun initBizCardInfoView(rawBitmap: ByteArray) {
        val bitmap = BitmapFactory.decodeByteArray(rawBitmap, 0, rawBitmap.size)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        bizcardInfo.addItemDecoration(itemDecoration)
        bizcardInfo.setHasFixedSize(true)
        adapter.loading(bitmap)
        bizcardInfo.adapter = adapter
    }

    private fun initToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    companion object {
        fun createIntent(activity: Activity): Intent =
                Intent(activity, ResultActivity::class.java)
    }
}
