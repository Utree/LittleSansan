@file:Suppress("DEPRECATION")
package com.sansan.example.bizcardocr.presentation.camera

import android.hardware.Camera
import android.view.SurfaceView
import android.view.SurfaceHolder
import android.view.WindowManager
import com.sansan.example.bizcardocr.utility.openCamera
import com.sansan.example.bizcardocr.utility.setDisplayOrientation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface CameraManagerAttachable {
    fun onAttach(surfaceView: SurfaceView)
}

class CameraManager(
        private val windowManager: WindowManager
): SurfaceHolder.Callback {

    private var camera: Camera? = null
    private var isStartingPreview: Boolean = false
    private lateinit var listener: CameraCallbackListener

    fun attach(surfaceView: SurfaceView, listener: CameraCallbackListener) {
        this.listener = listener
        surfaceView.holder.addCallback(this)
    }

    fun startPreview() {
        camera?.let {
            it.startPreview()
            isStartingPreview = true
        }
    }

    fun stopPreview() {
        camera?.let {
            it.stopPreview()
            isStartingPreview = false
        }
    }

    suspend fun takePicture(): ByteArray =
            suspendCancellableCoroutine { cont ->
                try {
                    camera?.takePicture(null, null) { data, _ ->
                        cont.resume(data)
                    }
                } catch (t: Throwable) {
                    cont.resumeWithException(t)
                }
            }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        camera =  openCamera()
        camera?.let {
            it.setPreviewDisplay(holder)
            it.setDisplayOrientation(windowManager)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        camera?.let {
            val params = it.parameters
            val focusModes = params.supportedFocusModes
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            }
            it.parameters = params
            startPreview()
        }
        camera?.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        camera?.release()
        camera = null
    }

    interface CameraCallbackListener {
        // TODO: 必要に応じて追加
    }
}
