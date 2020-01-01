package com.app.appchecker

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

class LogReadService : Service(){
    private val serviceBinder:IBinder = LocalBinder()
    companion object {
        var serviceIntent: Intent? = null
    }

    inner class LocalBinder: Binder() {
        fun getService(): LogReadService  = this@LogReadService
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

        fun readLogs(){
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

        GlobalScope.launch(Dispatchers.IO) {
            readLogs()
        }


        return super.onStartCommand(intent, flags, startId)
    }
}


