package com.example.zepkit

import android.app.Notification
import android.app.Notification.ProgressStyle
import android.app.Notification.ProgressStyle.Point
import android.app.Notification.ProgressStyle.Segment
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.IconCompat
import com.example.zepkit.OrderStatus.Arriving
import com.example.zepkit.OrderStatus.Confirmed
import com.example.zepkit.OrderStatus.Delivered
import com.example.zepkit.OrderStatus.EnRoute
import com.example.zepkit.OrderStatus.Preparing

object LiveNotificationManager {

    private const val CHANNEL_ID = "live_order_update_id"
    private const val CHANNEL_NAME = "Live Order Updates"

    // Zepkit Branding Colors
    private val WHITE = Color.parseColor("#FFFFFF")
    private val GREEN = Color.parseColor("#27AE60")
    private val GRAY = Color.parseColor("#FAB9B9")
    private val DONE = Color.parseColor("#27AE60")

    lateinit var notificationManager: NotificationManager
    lateinit var appContext: Context

    @RequiresApi(Build.VERSION_CODES.O)
    fun initialize(context: Context, notificationManager: NotificationManager) {
        this.notificationManager = notificationManager
        this.appContext = context.applicationContext
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_DEFAULT).apply {
            description = "Real-time tracking for your food delivery"
            enableLights(true)
            lightColor = WHITE
        }
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildBaseNotification(context: Context) =
        Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti_logo)
            .setLargeIcon(
                IconCompat.createWithResource(
                    appContext, R.drawable.img_3,
                ).toIcon(appContext),
            )
            .setOngoing(true)
            .setColor(WHITE)
            .setColorized(true)
            .setAutoCancel(false)

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun buildBaseProgressStyle(orderStatus: OrderStatus, currentProgress: Int): ProgressStyle {
        val style = ProgressStyle()

        val segments = listOf(
            Segment(25).setColor(if (currentProgress >= 25) GREEN else GRAY),
            Segment(25).setColor(if (currentProgress >= 50) GREEN else GRAY),
            Segment(25).setColor(if (currentProgress >= 75) GREEN else GRAY),
            Segment(25).setColor(if (currentProgress >= 100) DONE else GRAY)
        )

        val points = mutableListOf<Point>()
        if (currentProgress >= 25) points.add(Point(25).setColor(GREEN))
        if (currentProgress >= 50) points.add(Point(50).setColor(GREEN))
        if (currentProgress >= 75) points.add(Point(75).setColor(GREEN))
        if (currentProgress == 100) points.add(Point(100).setColor(GREEN))

        return style.setProgressSegments(segments).setProgressPoints(points)
    }

    @RequiresApi(Build.VERSION_CODES.BAKLAVA)
    fun updateNotification(orderStatus: OrderStatus, granularProgress: Int): Notification {
        val builder = buildBaseNotification(appContext)

        when (orderStatus) {
            Confirmed -> {
                builder.setContentTitle("Order Placed")
                    .setContentText("Restaurant is confirming your order...")
                    .setSmallIcon(R.drawable.order_confirmed)
            }
            Preparing -> {
                builder.setContentTitle("Kitchen is preparing your food")
                    .setContentText("Chef is adding the final touches!")
                    .setSmallIcon(R.drawable.preparing)
            }
            EnRoute -> {
                builder.setContentTitle("Valet is on the way")
                    .setContentText("Our delivery partner is heading to you")
                    .setSmallIcon(R.drawable.enroute)
            }
            Arriving -> {
                builder.setContentTitle("Almost there!")
                    .setContentText("Valet is arriving at your location")
                    .setSmallIcon(R.drawable.arriving)
            }
            Delivered -> {
                builder.setContentTitle("Order Delivered")
                    .setContentText("Hope you enjoy your meal!")
                    .setSmallIcon(R.drawable.delivered)
                    .setOngoing(false) // Allow dismissal once delivered
            }
        }

        // Apply the Smooth Progress Style
        val style = buildBaseProgressStyle(orderStatus, granularProgress)
            .setProgress(granularProgress)
            .setProgressTrackerIcon(
                IconCompat.createWithResource(
                    appContext,
                    when (orderStatus) {
                        EnRoute,
                        Arriving -> R.drawable.ic_noti_bike
                        Preparing -> R.drawable.ic_order_preparing
                        Confirmed -> R.drawable.ic_order_confirmed
                        Delivered -> R.drawable.ic_order_delivered
                        else -> R.drawable.ic_noti_dot
                    }
                ).toIcon(appContext)
            )

        val notification = builder.setStyle(style).build()
        return notification
    }
}