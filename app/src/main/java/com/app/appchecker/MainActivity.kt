package com.app.appchecker

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {
    private var foregroundServiceIntent:Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_LOGS) != PackageManager.PERMISSION_GRANTED){
            Log.d("App check run","permission deny")
        }else{
            Log.d("App check run","permission grant")
            if(LogReadService.serviceIntent == null){
                val foregroundServiceIntent = Intent(applicationContext, LogReadService::class.java)
                startService(foregroundServiceIntent)
            }else{
                foregroundServiceIntent = LogReadService.serviceIntent
                Toast.makeText(applicationContext,"이미 실행 중입니다.",Toast.LENGTH_SHORT)
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
