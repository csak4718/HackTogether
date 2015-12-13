package apt.hacktogether.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import apt.hacktogether.R;
import apt.hacktogether.activity.LoginActivity;

/**
 * Created by de-weikung on 12/13/15.
 */
public class MyReceiver extends ParsePushBroadcastReceiver {

    protected void onPushReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));


            //if (action.equalsIgnoreCase("com.packagename.UPDATE_STATUS")) {
            String title = "appname";
            if (json.has("title"))
                title = json.getString("title");

            String content = "mycontent";
            if (json.has("alert"))
                content = json.getString("alert");

            Uri uri = Uri.parse("hacktogether://");
            if (json.has("uri"))
                uri = Uri.parse(json.getString("uri"));
            Log.d("uri", "" + uri);

            generateNotification(context, title, uri, content);

        } catch (JSONException e) {
            Log.d("incomingreceiver", "JSONException: " + e.getMessage());
        }
    }

    private void generateNotification(Context context, String title, Uri uri, String contentText) {
        Log.d("incomingreceiver", "generate notification");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri, context, LoginActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);


        Uri ringUri = Uri.parse("android.resource://apt.hacktogether/raw/ding");
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_wb_cloudy_white_18dp)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{0,500,1000})
                .setAutoCancel(true)
                .setSound(ringUri);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1, mBuilder.build());

    }
}