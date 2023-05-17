package com.rmaprojects.eduwatch.service

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rmaprojects.parents.ParentsActivity
import io.karn.notify.Notify

class FcmService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val nData: Map<String, String> = message.data

        Notify.with(this)
            .content {
                title = nData["title"]
                text = nData["message"]
            }
            .meta {
                clickIntent = PendingIntent.getActivity(
                    this@FcmService,
                    0,
                    Intent(this@FcmService, ParentsActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
                clearIntent = PendingIntent.getService(
                    this@FcmService,
                    0,
                    Intent(this@FcmService, FcmService::class.java)
                        .putExtra("action", "clear_badges"),
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
            .show()
    }
}