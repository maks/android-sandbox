package com.manichord.sandbox;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SandboxActivity extends Activity implements OnClickListener
{
    private static final String TAG = SandboxActivity.class.getSimpleName();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ((Button) findViewById(R.id.notify_button)).setOnClickListener(this);
        ((Button) findViewById(R.id.strict_bad_button))
                .setOnClickListener(this);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyFlashScreen()
                .build());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.notify_button:
            Log.d(TAG, "Show Notify!");
            showNotification();
            break;
        case R.id.strict_bad_button:
            Log.d(TAG, "StrictMode!");
            tripStrictMode();
            break;
        }
    }

    private void showNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher) // notification icon
                .setContentTitle("Notification!") // title for notification
                .setContentText("Hello word") // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(this, SandboxActivity.class);
        PendingIntent pi = PendingIntent
                .getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void tripStrictMode() {
        for (int i = 0; i < 10; i++) {
            openUrl("http://manichord.com");
        }
    }

    private void openUrl(String urlText) {
        URL url = null;
        try {
            url = new URL(urlText);
        } catch (MalformedURLException e) {
            Log.e(TAG, "", e);
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.connect();// fire HTTP request
            int resCode = urlConnection.getResponseCode();
            if (resCode / 100 != 2) {
                try {
                    InputStream in = urlConnection.getInputStream();
                    while (in.read() != -1) {
                        // no op
                    }
                } catch (Exception e) {
                    // if e thrown well we just can't get any body for this
                    // error
                }
                Log.e(TAG, "HTTP Error (" + resCode + ")"
                        + urlConnection.getResponseMessage() + "->"
                        + urlText);
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            urlConnection.disconnect();
        }
    }
}
