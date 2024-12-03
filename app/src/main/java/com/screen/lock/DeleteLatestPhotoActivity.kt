package com.screen.lock

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.DataOutputStream
import java.io.File
import java.io.IOException
import kotlin.random.Random


class DeleteLatestPhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置布局文件
        setContentView(R.layout.activity_example)
        handleIncomingShare()

        val deleteButton: Button = findViewById(R.id.open_chatgpt_button)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        deleteButton.setOnClickListener {
            expandNotificationBar()
            val randomChance = Random.nextInt(100) // 生成一个 0 到 99 的随机整数
            if (randomChance < 98) { // 2% 的概率
                Toast.makeText(
                    this@DeleteLatestPhotoActivity,
                    "233",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                Toast.makeText(
                    this@DeleteLatestPhotoActivity,
                    "fwz",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        deleteButton.setOnLongClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
            true
        }


        // 设置全屏模式，隐藏状态栏和导航栏
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        // 防止屏幕息屏
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun handleIncomingShare() {
        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            when {
//                type.startsWith("text/") -> handleTextShare(intent) // 处理文本分享
                type.startsWith("image/") -> handleImageShare(intent) // 处理单张图片分享
            }
        } else if (Intent.ACTION_SEND_MULTIPLE == action && type != null && type.startsWith("image/")) {
//            handleMultipleImagesShare(intent) // 处理多张图片分享
            Toast.makeText(
                this@DeleteLatestPhotoActivity,
                "Error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun handleImageShare(intent: Intent) {
        val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
        if (imageUri != null) {
            val contentResolver = applicationContext.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)

            if (inputStream != null) {
                // 创建一个临时文件来存储图片
                val tempFile = File(applicationContext.cacheDir, "shared_image.jpg")
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()

                // 使用 FileProvider 生成 content:// 类型的 URI
                val sharedUri: Uri = FileProvider.getUriForFile(
                    applicationContext,
                    "${applicationContext.packageName}.fileprovider",
                    tempFile
                )

                // 将 URI 拷贝到剪切板
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newUri(contentResolver, "Shared Image", sharedUri)
                clipboard.setPrimaryClip(clip)

                val randomChance = Random.nextInt(100) // 生成一个 0 到 99 的随机整数
                if (randomChance < 2) { // 2% 的概率
                    Toast.makeText(this, "fwz", Toast.LENGTH_SHORT).show()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        }
    }




    fun expandNotificationBar() {
        try {
            // 获取Root权限
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)

            // 发送命令展开通知栏
            os.writeBytes("service call statusbar 1\n")
            os.flush()

            // 结束Shell会话
            os.writeBytes("exit\n")
            os.flush()

            process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // 保证全屏模式在焦点变化时依然有效
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}