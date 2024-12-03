package com.screen.lock

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.screen.lock.ui.theme.LockTheme
import java.io.DataOutputStream
import java.io.File


class MainActivity : ComponentActivity() {
    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)

            // 替换为 ChatGPT 应用的实际包名
            //val chatGptPackageName = "com.openai.chatgpt"
            //------------------------------------------------------------------------------------------------------------
            val chatGptPackageName = "com.ss.android.lark"
            val chatGptLaunchIntent: Intent? = packageManager.getLaunchIntentForPackage(chatGptPackageName)

            // 替换为 QQ 应用的实际包名
            val qqPackageName = "com.tencent.mobileqq"
            val qqLaunchIntent: Intent? = packageManager.getLaunchIntentForPackage(qqPackageName)

            val shortcuts = mutableListOf<ShortcutInfo>()

            val deletePhotoIntent = Intent(this, DeleteLatestPhotoActivity::class.java)
                .setAction(Intent.ACTION_VIEW) // 根据需要可以设置其他 Action

            val chatGptShortcut = ShortcutInfo.Builder(this, "id_open_pic_delete")
                .setShortLabel("息屏")
                .setLongLabel("息屏显示青春版")
                .setIcon(Icon.createWithResource(this, R.mipmap.tabler_home)) // 可以替换为 ChatGPT 的图标资源
                .setIntent(deletePhotoIntent)
                .build()
            shortcuts.add(chatGptShortcut)


            // 创建 QQ 快捷方式
            if (qqLaunchIntent != null) {
                val qqShortcut = ShortcutInfo.Builder(this, "id_open_qq")
                    .setShortLabel("QQ")
                    .setLongLabel("打开 QQ")
                    .setIcon(Icon.createWithResource(this, R.mipmap.tabler_home)) // 替换为 QQ 的图标资源
                    .setIntent(qqLaunchIntent)
                    .build()
                shortcuts.add(qqShortcut)
            } else {
                // 处理 QQ 应用未安装的情况
                // 例如，可以提示用户安装 QQ 应用
            }

            // 创建 ChatGPT 快捷方式
            if (chatGptLaunchIntent != null) {
                val chatGptShortcut = ShortcutInfo.Builder(this, "id_open_chatgpt")
                    .setShortLabel("飞书")
                    .setLongLabel("打开 飞书")
                    .setIcon(Icon.createWithResource(this, R.mipmap.tabler_home)) // 可以替换为 ChatGPT 的图标资源
                    .setIntent(chatGptLaunchIntent)
                    .build()
                shortcuts.add(chatGptShortcut)
            } else {
                // 处理 ChatGPT 应用未安装的情况
                // 例如，可以提示用户安装 ChatGPT 应用
            }



            // 设置动态快捷方式
            if (shortcuts.isNotEmpty()) {
                shortcutManager?.dynamicShortcuts = shortcuts
            }
        }


//        val deletePhotoIntent = Intent(this, DeleteLatestPhotoActivity::class.java)
//            .setAction(Intent.ACTION_VIEW)
//        startActivity(deletePhotoIntent)
//        finish();


        //------------------------------------------------------------------------------------------------------------
        // 设置启动动画持续时间（例如3秒）
        val animationDuration = 456// 3秒
        // 延迟执行某功能或跳转到主界面
        Handler(Looper.getMainLooper()).postDelayed({
            // 动画结束后执行的功能
            lockScreen()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        }, animationDuration.toLong())
        //------------------------------------------------------------------------------------------------------------



    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LockTheme {
        Greeting("Android")
    }
}

fun isDeviceRooted(): Boolean {
    val suPaths = arrayOf(
        "/system/bin/", "/system/xbin/", "/sbin/",
        "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/xbin/",
        "/data/local/bin/", "/data/local/"
    )
    return suPaths.any { path ->
        File(path + "su").exists()
    }
}

fun executeRootCommand(command: String): Boolean {
    return try {
        val process = Runtime.getRuntime().exec("su")
        val use = DataOutputStream(process.outputStream).use { os ->
            os.writeBytes("$command\n")
            os.writeBytes("exit\n")
            os.flush()
        }
        process.waitFor() == 0
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
fun lockScreen() {
    if (isDeviceRooted()) {
        val command = "input keyevent 26" // 26对应于电源键
        if (executeRootCommand(command)) {
            Log.d("LockScreen", "屏幕已锁定")
        } else {
            Log.e("LockScreen", "锁定屏幕失败")
        }
    } else {
        Log.e("LockScreen", "设备未Root，无法执行锁屏操作")
    }
}