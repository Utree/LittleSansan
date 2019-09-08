@file:Suppress("DEPRECATION")
package com.sansan.example.bizcardocr.presentation.camera

import android.content.Context
import android.view.SurfaceView
import android.util.AttributeSet

class CameraSurfaceView(
        context: Context,
        attrs: AttributeSet
) : SurfaceView(context, attrs) {
    init {
        if (context is CameraManagerAttachable) {
            context.onAttach(this)
        }
    }
}
