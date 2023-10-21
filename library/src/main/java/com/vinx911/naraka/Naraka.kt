package com.vinx911.naraka

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipFile
import kotlin.system.exitProcess

/**
 * Naraka - App崩溃后显示自定义的错误界面
 *
 */
class Naraka private constructor(
    private var context: Context,
    private val oldHandler: Thread.UncaughtExceptionHandler?,
    private val errorActivity: Class<out Activity>?,
    private val errorOccurredListener: ErrorOccurredListener?
) : Thread.UncaughtExceptionHandler {
    companion object {
        private const val TAG = "Naraka"

        private const val NARAKA_PACKAGE_NAME = "com.vinx911.compose.naraka"
        private const val DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os"
        private const val EXTRA_CRASH_MSG = "CrashMsg"
        private const val EXTRA_CRASH_DATA = "CrashData"

        /**
         * 初始化Naraka
         *
         * @param context 上下文
         * @param errorActivity 错误界面Activity类
         *
         */
        fun initialize(
            context: Context,
            errorActivity: Class<out Activity>? = null,
            errorOccurredListener: ErrorOccurredListener? = null
        ) {
            val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (oldHandler != null && oldHandler.javaClass.name.startsWith(NARAKA_PACKAGE_NAME)) {
                Log.e(TAG, "Naraka已经初始化!")
                return
            }

            if (oldHandler != null && !oldHandler.javaClass.name.startsWith(DEFAULT_HANDLER_PACKAGE_NAME)) {
                Log.e(TAG, "重要警告：已经安装了一个异常处理器${oldHandler.javaClass.name}, 异常处理器将被覆盖为Naraka")
            }

            val naraka = Naraka(context, oldHandler, errorActivity, errorOccurredListener)
            Thread.setDefaultUncaughtExceptionHandler(naraka)
        }

        private fun killCurrentProcess() {
            Process.killProcess(Process.myPid())
            exitProcess(10)
        }

        /**
         * 关闭App
         */
        fun closeApplication(activity: Activity) {
            activity.finish()
            killCurrentProcess()
        }

        private fun getRestartIntent(activity: Activity): Intent {
            val packageName = activity.packageName
            val defaultIntent = activity.packageManager.getLaunchIntentForPackage(packageName)
            if (defaultIntent != null) {
                return defaultIntent
            }
            throw IllegalStateException("无法确定 $packageName 的默认活动.")
        }

        /**
         * 重启App
         */
        fun restartApplication(activity: Activity) {
            val intent = getRestartIntent(activity)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            activity.finish()
            activity.startActivity(intent)
            killCurrentProcess()
        }

        /**
         * 从intent中获取StackTrace
         */
        fun getStackTraceFromIntent(intent: Intent): String {
            return intent.getStringExtra(EXTRA_CRASH_DATA) ?: ""
        }

        /**
         * 从intent中获取异常消息
         */
        fun getCrashMsgFromIntent(intent: Intent): String {
            return intent.getStringExtra(EXTRA_CRASH_MSG) ?: ""
        }

        private fun getBuildDateAsString(context: Context, dateFormat: DateFormat): String? {
            var buildDate: Long
            try {
                val ai = context.packageManager.getApplicationInfo(context.packageName, 0)
                val zf = ZipFile(ai.sourceDir)

                val ze = zf.getEntry("classes.dex")
                buildDate = ze.time
                zf.close()
            } catch (e: java.lang.Exception) {
                buildDate = 0
            }

            return if (buildDate > 631152000000L) dateFormat.format(Date(buildDate)) else null
        }

        private fun getVersionName(context: Context): String {
            return try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName
            } catch (e: java.lang.Exception) {
                "Unknown"
            }
        }

        private fun getDeviceModelName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        private fun capitalize(s: String?): String {
            if (s.isNullOrEmpty()) {
                return ""
            }

            return s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        /**
         * 获取错误详细信息
         */
        fun getErrorDetailsFromIntent(context: Context, intent: Intent): String {
            val currentDate = Date()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val buildDateAsString = getBuildDateAsString(context, dateFormat)
            val versionName = getVersionName(context)

            var errorDetails = ""

            errorDetails += "Build version: $versionName \n"
            if (buildDateAsString != null) {
                errorDetails += "Build date: $buildDateAsString \n"
            }

            errorDetails += "Current date: " + dateFormat.format(currentDate) + " \n"
            errorDetails += "Device: " + getDeviceModelName() + " \n"
            errorDetails += "OS version: Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ") \n \n"
            errorDetails += "Stack trace:  \n"
            errorDetails += getStackTraceFromIntent(intent)

            return errorDetails
        }
    }

    private fun getErrorActivity(): Class<out Activity> {
        return errorActivity ?: NarakaDefaultErrorActivity::class.java
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Log.e(TAG, "App 已经崩溃, 执行 Naraka 的 UncaughtExceptionHandler", throwable)

        try {
            errorOccurredListener?.onErrorOccurred(throwable)

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            val stackTraceString = sw.toString()
            val crashMsg = throwable.message

            val application = context.applicationContext as Application
            val errorActivity = getErrorActivity()
            val intent = Intent(application, errorActivity).apply {
                putExtra(EXTRA_CRASH_MSG, crashMsg)
                putExtra(EXTRA_CRASH_DATA, stackTraceString)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            application.startActivity(intent)
            killCurrentProcess()
        } catch (e: Exception) {
            Log.e(TAG, "uncaughtException: $e")
            oldHandler?.uncaughtException(thread, throwable)
        }
    }

    fun interface ErrorOccurredListener {
        fun onErrorOccurred(throwable: Throwable)
    }
}