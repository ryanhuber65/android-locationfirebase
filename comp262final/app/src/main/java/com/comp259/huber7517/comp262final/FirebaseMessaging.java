package com.comp259.huber7517.comp262final;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            String dataPayload = remoteMessage.getData().toString();
            Map<String, String> msgData = remoteMessage.getData();
            //check to make sure the keys contain "lat" and "lon"
            if(msgData.containsKey("lat") && msgData.containsKey("lon") && msgData.containsKey("desc")){

                //assign the key values to string variables "lat" and "lon"
                String lat = msgData.get("lat").toString();
                String lon = msgData.get("lon").toString();
                String desc = msgData.get("desc").toString();

                //String notificationPayload = remoteMessage.getNotification().getBody();

                boolean numeric = true;
                //check to make sure the lat and lon variables are numeric
                try{
                    Double numlat = Double.parseDouble(lat);
                    Double numlon = Double.parseDouble(lon);

                }catch(NumberFormatException e){
                    //if the exception is caught, numeric is false
                    numeric = false;
                }
                if(numeric){
                    //format the time and convert to string
                    long time = remoteMessage.getSentTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    String formattedTime = formatter.format(calendar.getTime());

                    //package the data into an intent
                    Intent intent = new Intent(Global.PUSH_NOTIFICATION);
                    intent.putExtra(Global.EXTRA_MESSAGE, dataPayload);
                    //intent.putExtra(Global.NOTIFICATION_MESSAGE, notificationPayload);
                    intent.putExtra(Global.NOTIFICATION_MESSAGE, desc);
                    intent.putExtra(Global.LATITUDE, lat);
                    intent.putExtra(Global.LONGITUDE, lon);
                    intent.putExtra(Global.TIME, formattedTime);

                    //send the broadcast to pass the intent to the MapsActivity
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                    //log the data being sent - for testing
                    Log.i(TAG, "Data Message= " + dataPayload);
                    Log.i(TAG, "Desc Message= " + desc);
                    Log.i(TAG, "Lat Message= " + lat);
                    Log.i(TAG, "Lon Message= " + lon);
                }

            }

        }
        // Check if message contains a notification payload and send broadcast to MapsActivity
        if (remoteMessage.getNotification() != null) {

            String notificationPayload = remoteMessage.getNotification().getBody();

            Intent intent = new Intent(Global.PUSH_NOTIFICATION);
            intent.putExtra(Global.NOTIFICATION_MESSAGE, notificationPayload);


            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            Log.i(TAG, "Notification Message= " + notificationPayload);
        }
    }
}
