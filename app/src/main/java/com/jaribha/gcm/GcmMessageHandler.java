package com.jaribha.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.jaribha.R;
import com.jaribha.activity.HomeScreenActivity;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.activity.ProjectDetailsTabs;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class GcmMessageHandler extends GcmListenerService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    private boolean msg, comment, update, payment;

    private String projectId;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        // String message = data.getString("alert");
        //{m={"msg":"payment success","type":"paymentsuccess"}, collapse_key=do_not_collapse}
        Log.e("message", data + "");
        JSONObject dataObj;
        try {
            dataObj = new JSONObject(data.getString("m"));
            if (dataObj.getString("type").equals("chat")) {
                msg = true;
            } else if (dataObj.getString("type").equals("comment")) {
                if (dataObj.has("project_id")) {
                    comment = true;
                    projectId = dataObj.getString("project_id");
                }
            } else if (dataObj.getString("type").equals("payment")) {
                payment = true;
            } else if (dataObj.getString("type").equals("projectupdate")) {
                if (dataObj.has("project_id")) {
                    update = true;
                    projectId = dataObj.getString("project_id");
                }
            } else if (dataObj.getString("type").equals("paymentsuccess")) {
                Intent intent = new Intent(Constants.Action_PAYMENT);
                intent.putExtra(Constants.DATA, dataObj.getString("msg"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            createNotification(dataObj.getString("msg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Creates notification based on title and body received
    private void createNotification(String body) {
        Context context = getBaseContext();
        SessionManager sessionManager = SessionManager.getInstance(context);
        Object user = sessionManager.getUser();
        Intent notificationIntent;
        if (msg) {
            if (user != null)
                notificationIntent = new Intent(context, HomeScreenActivity.class).putExtra("pos", 8);
            else
                notificationIntent = new Intent(context, LoginScreenActivity.class);
        } else if (comment) {
            JaribhaPrefrence.setPref(context, Constants.PROJECT_ID, projectId);
            if (user != null)
                notificationIntent = new Intent(context, ProjectDetailsTabs.class).putExtra("comment_push", true);
            else
                notificationIntent = new Intent(context, LoginScreenActivity.class);
        } else if (update) {
            JaribhaPrefrence.setPref(context, Constants.PROJECT_ID, projectId);
            if (user != null)
                notificationIntent = new Intent(context, ProjectDetailsTabs.class).putExtra("update_push", true);
            else
                notificationIntent = new Intent(context, LoginScreenActivity.class);
        } else if (payment) {
            if (user != null)
                notificationIntent = new Intent(context, HomeScreenActivity.class);//.putExtra("pos", 8);
            else
                notificationIntent = new Intent(context, LoginScreenActivity.class);
        } else {
            if (user != null)
                notificationIntent = new Intent(context, HomeScreenActivity.class);
            else
                notificationIntent = new Intent(context, LoginScreenActivity.class);
        }

        // This ensures that the back button follows the recommended convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(HomeScreenActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_jaribha_icon_1024)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentText(body)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }
}