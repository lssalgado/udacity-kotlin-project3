package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*


const val FILE_NAME = "fileName"
const val DOWNLOAD_STATUS = "downloadStatus"

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
        var bDownloadStatus = false
        extras?.let {
            sFileName = it.getString(FILE_NAME, sFileName)
            bDownloadStatus = it.getBoolean(DOWNLOAD_STATUS)
        }

        fileName.text = sFileName
        downloadStatus.text = bDownloadStatus.toString()

        okButtom.setOnClickListener {
            MainActivity.start(this)
        }
    }

    override fun onResume() {
        super.onResume()
        // Removes all pending notifications
        notificationManager.cancelAll()
    }
}
