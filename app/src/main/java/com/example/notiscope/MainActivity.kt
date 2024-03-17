package com.example.notiscope
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


@Composable
fun MyScreen() {
    val isServiceRunning = remember { mutableStateOf(false) }

    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    val serviceIntent = Intent(context, SensorService::class.java)
                    if (!isServiceRunning.value) {
                        ContextCompat.startForegroundService(context, serviceIntent)
                        isServiceRunning.value = true
                    } else {
                        context.stopService(serviceIntent)
                        isServiceRunning.value = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isServiceRunning.value) "Stop Sensor Service" else "Start Sensor Service")
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkDoNotDisturbPermission()
        setContent {
            MyScreen()
        }
    }
    private fun checkDoNotDisturbPermission() {
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }
}