package com.codev.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    private val TAG = "FirebaseService"

    // 푸시 알림을 표시할 채널 ID (API 26 이상에서 필요)
    private val CHANNELID = "CODEV"
    private val CHANNELNAME = "CHAT"

    // 토큰 가져오기
    fun getFirebaseToken(context:Context) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d(TAG, "token=${it}")
            UserSharedPreferences.setFCMToken(it)
        }
    }

    // 토큰 갱신
    override fun onNewToken(token: String)  {
        Log.d(TAG, "new Token: $token")
        UserSharedPreferences.setFCMToken(token)
    }

    // FCM 수신
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {notification ->
            Log.d(TAG, "Message Notification Body: ${notification.body}")

            val title = notification.title
            val message = notification.body
            title?.let { nonNullTitle ->
                message?.let { nonNullMessage ->
                    showNotification(applicationContext, nonNullTitle, nonNullMessage)
                }
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /** 알림 생성 메서드 */
    private fun showNotification(context: Context, title: String, message: String) {
        val notificationId = System.currentTimeMillis().toInt()

        // 알림 소리
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 알림에 대한 UI 정보, 작업
        val notificationBuilder = NotificationCompat.Builder(this, CHANNELID)
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher))
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(soundUri)

        // 알림을 클릭하면 앱으로 이동하도록 인텐트를 설정합니다.
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android Oreo 이상에서는 알림 채널을 생성하여 설정해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNELID,
                CHANNELNAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 알림을 표시합니다.
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}

/**
"notificationBuilder" 알림 생성시 여러가지 옵션을 이용해 커스텀 가능.
setSmallIcon : 작은 아이콘 (필수)
setContentTitle : 제목 (필수)
setContentText : 내용 (필수)
setColor : 알림내 앱 이름 색
setWhen : 받은 시간 커스텀 ( 기본 시스템에서 제공합니다 )
setShowWhen : 알림 수신 시간 ( default 값은 true, false시 숨길 수 있습니다 )
setOnlyAlertOnce : 알림 1회 수신 ( 동일 아이디의 알림을 처음 받았을때만 알린다, 상태바에 알림이 잔존하면 무음 )
setContentTitle : 제목
setContentText : 내용
setFullScreenIntent : 긴급 알림 ( 자세한 설명은 아래에서 설명합니다 )
setTimeoutAfter : 알림 자동 사라지기 ( 지정한 시간 후 수신된 알림이 사라집니다 )
setContentIntent : 알림 클릭시 이벤트 ( 지정하지 않으면 클릭했을때 아무 반응이 없고 setAutoCancel 또한 작동하지 않는다 )
setLargeIcon : 큰 아이콘 ( mipmap 에 있는 아이콘이 아닌 drawable 폴더에 있는 아이콘을 사용해야 합니다. )
setAutoCancel : 알림 클릭시 삭제 여부 ( true = 클릭시 삭제 , false = 클릭시 미삭제 )
setPriority : 알림의 중요도를 설정 ( 중요도에 따라 head up 알림으로 설정할 수 있는데 자세한 내용은 밑에서 설명하겠습니다. )
setVisibility : 잠금 화면내 알림 노출 여부
Notification.VISIBILITY_PRIVATE : 알림의 기본 정보만 노출 (제목, 타이틀 등등)
Notification.VISIBILITY_PUBLIC : 알림의 모든 정보 노출
Notification.VISIBILITY_SECRET : 알림의 모든 정보 비노출
 */