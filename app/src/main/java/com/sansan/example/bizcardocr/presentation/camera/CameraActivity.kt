package com.sansan.example.bizcardocr.presentation.camera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.SurfaceView
import android.view.View
import com.sansan.example.bizcardocr.BizCardOCRApplication
import com.sansan.example.bizcardocr.R
import com.sansan.example.bizcardocr.presentation.result.ResultActivity
import com.sansan.example.bizcardocr.utility.CancelableCoroutineScope
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraActivity : AppCompatActivity(), CameraManagerAttachable, CameraManager.CameraCallbackListener {
    private val cameraManager by lazy { CameraManager(this.windowManager) }
    private val coroutineScope = CancelableCoroutineScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        cameraManager.startPreview()
        setFullScreen()
        shutterButton.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        cameraManager.stopPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onAttach(surfaceView: SurfaceView) {
        cameraManager.attach(surfaceView, this)
    }

    private fun initViews() {
        shutterButton.setOnClickListener {
            shutterButton.isEnabled = false
            takePicture()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setFullScreen() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun takePicture() {
        coroutineScope.launch(Dispatchers.Default) {
            try {
                // Activity間の値の橋渡しとして、今回はApplicationを使う(Intentだとデータが大きすぎて送れない)
                // ※ 一時ファイルとして保持することも検討する
                val bizCardOcrApplication = application as BizCardOCRApplication
                bizCardOcrApplication.tempRawPicture = cameraManager.takePicture()
                showResult()
            } catch (t: Throwable) {
                shutterButton.isEnabled = true
            }
        }
    }

    private fun showResult() {
        ResultActivity.createIntent(this).let(this::startActivity)
    }

    companion object {
        fun createIntent(activity: Activity): Intent =
                Intent(activity, CameraActivity::class.java)
    }
}
