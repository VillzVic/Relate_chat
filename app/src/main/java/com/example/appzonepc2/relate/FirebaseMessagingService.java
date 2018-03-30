package com.example.appzonepc2.relate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.example.appzonepc2.relate.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by appzonepc2 on 15/03/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String icon = remoteMessage.getNotification().getIcon();

        String clickAction = remoteMessage.getNotification().getClickAction(); //com.example.appzonepc2.relate_TARGET_NOTIFICATION

        String sender_user_id = remoteMessage.getData().get("userid").toString();




        Intent resultIntent = new Intent(clickAction); //check the activity that has the click action in the manifest, in this case it is profile activity
        resultIntent.putExtra("visit_user_data", sender_user_id);

        PendingIntent resultPendingIntent =  PendingIntent.getActivity(this,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder mBuiler = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentIntent(resultPendingIntent);
//                .setSmallIcon()
//                .setContentIntent()





        int notificationId = (int) System.currentTimeMillis();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(notificationId, mBuiler.build());

    }
}
