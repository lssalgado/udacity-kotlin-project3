package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

const val FILE_NAME = "fileName"
const val DOWNLOAD_STATUS = "downloadStatus"
const val DOWNLOAD_ID = "downloadId"

class DetailActivity : AppCompatActivity() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(
            NotificationManager::class.java
        )
    }
    private val downloadManager: DownloadManager by lazy {
        getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    private var fileDetails: FileDetails? by Delegates.observable<FileDetails?>(null) { _, _, new ->
        CoroutineScope(Dispatchers.Main).launch {
            new?.let {
                fileId.text = new.id.toString()
                mediaType.text = new.mediaType
                size.text = new.size.toString()
                lastModified.text = new.lastModified
                uri.text = new.uri
                localUri.text = new.localUri

                if (new.localUri.isNotEmpty()) {
                    openFileButton.isEnabled = true
                }
            }
        }
    }

    // Base on Martin Himmel answer https://stackoverflow.com/a/31711281
    private fun openFile(localUri: String, mediaType: String) {
        val uri = Uri.parse(localUri)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mediaType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        openFileButton.setOnClickListener {
            fileDetails?.let {
                openFile(it.localUri, it.mediaType)
            }
        }

        handleIntent()
    }

    private fun handleIntent() {
        val extras = intent.extras
        var sFileName = getString(R.string.missing_file_name_key)
        var downloadText = getString(R.string.fail)
        var textColor = getColor(R.color.red)
        var downloadId = -1L
        extras?.let {
            sFileName = it.getString(FILE_NAME, sFileName)
            val bDownloadStatus = it.getBoolean(DOWNLOAD_STATUS)
            downloadId = it.getLong(DOWNLOAD_ID, -1)
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

        getFileInfo(downloadId)
    }

    private fun getFileInfo(id: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            val query = DownloadManager.Query()
            query.setFilterById(id)
            val cursor = downloadManager.query(query)


            if (cursor.moveToFirst()) {
                val fileId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
                val mediaType =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
                val size =
                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val lastModifiedInt =
                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP))
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val lastModified = formatter.format(lastModifiedInt)
                val uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                val localUri =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                fileDetails = FileDetails(
                    fileId,
                    mediaType,
                    size,
                    lastModified,
                    uri,
                    localUri
                )
            }
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
