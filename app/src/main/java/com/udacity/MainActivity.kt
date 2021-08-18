package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.util.DownloadStatus
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
    private val selectOption: String by lazy { getString(R.string.please_select) }
    private val enterValidUrl: String by lazy { getString(R.string.enter_valid_url) }
    private lateinit var toastText: String
    private lateinit var fileName: String
    private val downloadManager: DownloadManager by lazy {
        getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    private var isDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        toastText = selectOption

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            customUrl.visibility = if (checkedId == R.id.custom) {
                toastText = enterValidUrl
                customUrl.isEnabled = true
                View.VISIBLE
            } else {
                toastText = selectOption
                customUrl.isEnabled = false
                View.GONE
            }
        }
        custom_button.setOnClickListener {
            download()
        }
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
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

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != null && intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                // Will only speed up the animation if still running
                if (custom_button.buttonState == ButtonState.Loading) {
                    custom_button.buttonState = ButtonState.Completed
                }

                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)
                var downloadStatus = DownloadStatus.FAIL
                var statusText = "failed"
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloadStatus = DownloadStatus.SUCCESS
                        statusText = "finished successfully"
                    }
                }

                notificationManager.sendNotification(
                    "Download $statusText!!",
                    applicationContext,
                    fileName,
                    downloadStatus,
                    id
                )
            }
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

        waitForDownloadToBegin()

        custom_button.buttonState = ButtonState.Loading
    }

    private fun getURLFromSelectedOption(): String? {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.glide -> {
                fileName =
                    findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()
                GLIDE_URL
            }
            R.id.loadApp -> {
                fileName =
                    findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()
                LOAD_APP_URL
            }
            R.id.retrofit -> {
                fileName =
                    findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()
                RETROFIT_URL
            }
            R.id.custom -> validateUrl()
            else -> null
        }
    }

    private fun validateUrl(): String? {
        val typedUrl = customUrl.text.toString()
        return if (Patterns.WEB_URL.matcher(typedUrl).matches()) {
            fileName = typedUrl
            typedUrl
        } else {
            null
        }
    }

    private fun waitForDownloadToBegin() {
        CoroutineScope(Dispatchers.Default).launch {
            while (!isDownloading) {
                checkIfDownloadBegan()
            }
            sendNotification()
        }
    }

    private fun sendNotification() {
        CoroutineScope(Dispatchers.Main).launch {
            notificationManager.sendNotification(
                "Download in progress!!",
                applicationContext,
                fileName,
                DownloadStatus.IN_PROGRESS
            )
        }
    }

    private fun checkIfDownloadBegan() {
        val query = DownloadManager.Query()

        query.setFilterById(downloadID)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val downloaded =
                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

            if (downloaded > 0) {
                isDownloading = true
            }
        }
        cursor.close()
    }

    companion object {
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"

        fun start(activity: Activity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
