package com.quizo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    Bitmap bitmap;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message,imageUri,titlee,author,date;

            message = remoteMessage.getData().get("desc");
            titlee = remoteMessage.getData().get("title");
            author = remoteMessage.getData().get("auth");
            date = remoteMessage.getData().get("date");
        if(remoteMessage.getData().get("url") != null){
         imageUri = remoteMessage.getData().get("url");
            //To get a Bitmap image from the URL received
            bitmap = getBitmapfromUrl(imageUri);

            sendNotification(author,date,titlee, message, bitmap,imageUri);
        } else {
            sendNotification(author, date, titlee,message,null,null);
        }


    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */

    private void sendNotification(String author, String date, String titlee, String messageBody, Bitmap image, String imageUri) {
        Intent resultIntent = new Intent(MyFirebaseMessagingService.this, SharedeleClass.class);
        resultIntent.putExtra("desc",messageBody);
        resultIntent.putExtra("url",imageUri);
        resultIntent.putExtra("title",titlee);
        resultIntent.putExtra("auth",author);
        resultIntent.putExtra("date",date);
        resultIntent.putExtra("activity","first");
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),"quizo")
                .setLargeIcon(image)/*Notification icon image*/
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titlee)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image))/*Notification with Image*/
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(87 /* ID of notification */, notificationBuilder.build());
    }

    /*
     *To get a Bitmap image from the URL received
     * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    public  String returnvalue(String mes){
        return mes;

    }

}
