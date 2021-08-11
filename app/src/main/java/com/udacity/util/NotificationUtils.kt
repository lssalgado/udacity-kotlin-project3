/*
 * Copyright (C) 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on https://github.com/udacity/android-kotlin-notifications-fcm/blob/master/app/src/main/java/com/example/android/eggtimernotifications/util/NotificationUtils.kt
 */

package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.graphics.BitmapFactory
import android.os.Bundle
import com.udacity.DOWNLOAD_STATUS
import com.udacity.DetailActivity
import com.udacity.FILE_NAME
import com.udacity.R

// Notification ID.
private const val NOTIFICATION_ID = 0

/**
 * Builds and delivers the notification.
 *
 * @param messageBody, notification text.
 * @param applicationContext, activity context.
 */
fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    fileName: String,
    downloadStatus: Boolean
) {
    val bundle = Bundle()

    bundle.putString(FILE_NAME, fileName)
    bundle.putBoolean(DOWNLOAD_STATUS, downloadStatus)

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtras(bundle)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT,
        bundle
    )

    val downloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_baseline_cloud_download_24
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setLargeIcon(downloadImage)
        .addAction(
           R.drawable.ic_baseline_cloud_download_24,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        )
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}
