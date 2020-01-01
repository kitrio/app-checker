package com.app.appchecker.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import android.content.Context
import com.app.appchecker.MainActivity
import com.app.appchecker.R


class LogReadService : Service(){
    private val serviceBinder:IBinder = LocalBinder()

    companion object {
        var serviceIntent: Intent? = null
    }

    inner class LocalBinder: Binder() {
        fun getService(): LogReadService = this@LogReadService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return serviceBinder
    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(applicationContext,"기록 시작",Toast.LENGTH_LONG).show()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceIntent = intent
        serviceNotification()

        GlobalScope.launch(Dispatchers.IO) {
            readLogs()
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun readLogs(){
        val logcat:Process = Runtime.getRuntime().exec(arrayOf("logcat", "-v threadtime D/VibratorService"))
        val logVibrate = StringBuilder()//StringBuilder logVibrate = new StringBuilder
        val br = BufferedReader(InputStreamReader(logcat.inputStream))
        var line: String
        while (true){
            line = br.readLine()
            if(line.contains("opPkg")){
                logVibrate.append(line)
                Log.println(Log.INFO,"this is app",logVibrate.length.toString())
                logVibrate.setLength(0)
            }
        }
    }

    private fun serviceNotification(){
        val builder = NotificationCompat.Builder(this, "1")
        builder.setSmallIcon(R.drawable.ic_check_black_24dp)
        val style = NotificationCompat.BigTextStyle()
        style.bigText("앱 체크가 실행중입니다. 확인하려면 누르세요")
        style.setBigContentTitle(null)
        style.setSummaryText("측정 중")
        builder.setContentText(null)
        builder.setContentTitle(null)
        builder.setOngoing(true)
        builder.setStyle(style)
        builder.setWhen(0)
        builder.setShowWhen(false)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "1",
                    "logService",
                    NotificationManager.IMPORTANCE_NONE
                )
            )
        }
        val notification = builder.build()
        startForeground(1, notification)
    }
}


