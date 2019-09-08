package com.sansan.example.bizcardocr.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sansan.example.bizcardocr.R
import com.sansan.example.bizcardocr.presentation.camera.CameraActivity
import kotlinx.android.synthetic.main.activity_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener {
            // TODO: 課題1 (Runtime Permissionを実装してカメラ画面、そしてカメラ映像を表示できるようにしてください。)
            // 下記showCameraメソッドはそのまま呼び出すとカメラ画像は表示できるものの、Runtime Permissionが未実装のためカメラ映像を表示することができません。
            // そのためRuntime Permissionを実装し、許可ダイアログで許可を取ってからカメラ画面を表示するように修正してください。

            showCameraWithPermissionCheck()
        }
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    fun showCamera() {
        CameraActivity.createIntent(this).let(this::startActivity)
    }

}
