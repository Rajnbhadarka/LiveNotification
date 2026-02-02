package com.example.zepkit

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    // Launcher for notification permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Permission", "Notification Permission Granted")
            onPermissionGranted()
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                // Permanently denied
                showSettingsDialog(context)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onPermissionGranted()
        }
    }
}


fun showSettingsDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.str_need_permissions))
        .setMessage(
            context.getString(
                R.string.str_this_app_needs_permission_to_use_this_feature_you_can_grant_them_in_app_settings
            )
        )
        .setPositiveButton(context.getString(R.string.str_goto_settings)) { dialog, _ ->
            dialog.dismiss()
            openSettings(context)
        }
        .setNegativeButton(context.getString(R.string.str_cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .show()
}

fun openSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}
