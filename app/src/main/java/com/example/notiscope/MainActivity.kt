package com.example.notiscope
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyScreen()
        }
    }
}

@Composable
fun MyScreen(viewModel: SensorViewModel = viewModel()) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val sensorListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (viewModel.isSensorEnabled) {
                        viewModel.onSensorChanged(it)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(viewModel.isSensorEnabled) {
        if (viewModel.isSensorEnabled) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            sensorManager.unregisterListener(sensorListener)
        }
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    Surface(
        color = if (viewModel.isFacingDown) Color.Blue else Color.Red,
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            color = if (viewModel.isFacingDown) Color.Blue else Color.Red,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = { viewModel.toggleSensorState() }
                ) {
                    Text(text = if (viewModel.isSensorEnabled) stringResource(R.string.sensor_on) else stringResource(R.string.sensor_off))
                }
            }
        }

    }
}

class SensorViewModel : ViewModel() {
    var isFacingDown by mutableStateOf(false)
    var isSensorEnabled by mutableStateOf(true)

    fun onSensorChanged(event: SensorEvent) {
        val z = event.values[2]
        isFacingDown = z < -9 && z > -10
    }

    fun toggleSensorState() {
        isSensorEnabled = !isSensorEnabled
    }
}
