package com.example.zepkit

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.zepkit.service.NotificationService
import com.example.zepkit.ui.theme.ZepkitTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<OrderViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZepkitTheme {
                val context = LocalContext.current
                val notificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                LiveNotificationManager.initialize(context.applicationContext, notificationManager)
                val orderStatus = viewModel.orderState.collectAsState().value

                LaunchedEffect(orderStatus) {
                    val intent = Intent(context, NotificationService::class.java).apply {
                        putExtra("ORDER_DATA", orderStatus)
                    }
                    ContextCompat.startForegroundService(context, intent)
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {


    NotificationPermissionHandler {
        // Permission granted
        Log.d("Permission", "Ready to show notifications")
    }


    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ZepkitTheme {
        Greeting("Android")
    }
}

