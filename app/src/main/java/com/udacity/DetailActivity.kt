package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

const val FILE_NAME = "fileName"
const val DOWNLOAD_STATUS = "downloadStatus"
const val DOWNLOAD_ID = "downloadId"

class DetailActivity : AppCompatActivity() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(
            NotificationManager::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        handleIntent()
    }

    private fun handleIntent() {
        val extras = intent.extras
        var sFileName = getString(R.string.missing_file_name_key)
        var downloadText = getString(R.string.fail)
        var textColor = getColor(R.color.red)
        extras?.let {
            sFileName = it.getString(FILE_NAME, sFileName)
            val bDownloadStatus = it.getBoolean(DOWNLOAD_STATUS)
            if (bDownloadStatus) {
                downloadText = getString(R.string.success)
                textColor = getColor(R.color.colorPrimaryDark)
            }
        }

        fileName.text = sFileName
        downloadStatus.text = downloadText
        downloadStatus.setTextColor(textColor)

        okButton.setOnClickListener {
            MainActivity.start(this)
        }
    }

    override fun onResume() {
        super.onResume()
        // Removes all pending notifications
        notificationManager.cancelAll()
    }

    data class FileDetails(
        val id: Long,
        val mediaType: String,
        val size: Long,
        val lastModified: String,
        val uri: String,
        val localUri: String
    )
}
