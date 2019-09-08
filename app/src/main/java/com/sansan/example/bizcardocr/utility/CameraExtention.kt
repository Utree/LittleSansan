@file:Suppress("DEPRECATION")
package com.sansan.example.bizcardocr.utility

import android.hardware.Camera
import android.view.Surface
import android.view.WindowManager

fun Camera.setDisplayOrientation(windowManager: WindowManager) {
    val cameraId = cameraId ?: return

    val info = Camera.CameraInfo()
    Camera.getCameraInfo(cameraId, info)

    var degrees = 0
    when (windowManager.defaultDisplay.rotation) {
        Surface.ROTATION_0 -> degrees = 0
        Surface.ROTATION_90 -> degrees = 90
        Surface.ROTATION_180 -> degrees = 180
        Surface.ROTATION_270 -> degrees = 270
    }

    var result: Int
    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        result = (info.orientation + degrees) % 360
        result = (360 - result) % 360 // compensate the mirror
    } else { // back-facing
        result = (info.orientation - degrees + 360) % 360
    }
    setDisplayOrientation(result)
}

fun openCamera(): Camera? {
    val cameraId = cameraId ?: return null
    return Camera.open(cameraId)
}

private val cameraId: Int? =
        getCameraId(false)  ?: getCameraId(true)

// 基本的にはバックカメラを使ってほしいが、フロントカメラしか使えない端末用にisFrontフラグで前後カメラを使い分ける
private fun getCameraId(isFront: Boolean): Int? {
    val numberOfCameras = Camera.getNumberOfCameras()
    for (i in 0 until numberOfCameras) {
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(i, cameraInfo)

        if (isFront) {
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i
            }
        } else {
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i
            }
        }
    }
    return null
}
