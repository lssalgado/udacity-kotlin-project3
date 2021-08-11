package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private val notificationManager: NotificationManager by lazy {
        getSystemService(
            NotificationManager::class.java
        )
    }
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var toast: Toast
    private val toastText: String by lazy { getString(R.string.please_select) }
    private val downloadManager: DownloadManager by lazy {
        getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }
        createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onResume() {
        super.onResume()
        // Removes all pending notifications
        notificationManager.cancelAll()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("TESTE", "onReceive!!")
            Log.e("TESTE", "$downloadID")
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Log.e("TESTE", "$id")

            notificationManager.sendNotification("Download finished!!", applicationContext)
        }
    }

    private fun download() {
        val url = getURLFromSelectedOption()

        if (url == null) {
            if (::toast.isInitialized) {
                toast.cancel()
            }
            toast = Toast.makeText(
                applicationContext,
                toastText,
                Toast.LENGTH_SHORT
            )
            toast.show()
            return
        }

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getURLFromSelectedOption(): String? {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.glide -> GLIDE_URL
            R.id.loadApp -> LOAD_APP_URL
            R.id.retrofit -> RETROFIT_URL
            else -> null
        }
    }

    companion object {
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"
    }
}
