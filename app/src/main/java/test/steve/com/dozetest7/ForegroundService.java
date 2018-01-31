package test.steve.com.dozetest7;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ForegroundService extends Service {
  private static final String LOG_TAG = "ForegroundService";

  public static boolean serviceRunning = false;

  private final IBinder mBinder = new LocalBinder();

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
      serviceRunning = true;
      Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
      Log.i(LOG_TAG, "Received Start Foreground Intent ");

      startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, buildNotification());

      testNetwork();
    } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
      Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
      Log.i(LOG_TAG, "Received Stop Foreground Intent");
      serviceRunning = false;
      stopForeground(true);
      stopSelf();
    }
    return START_STICKY;
  }

  private Notification buildNotification() {
    // build notification
    Intent intent = new Intent(this, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(
        this,
        0,
        intent,
        PendingIntent.FLAG_NO_CREATE
    );

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setContentTitle("HelloApp")
        .setContentText("The Service is running foreground")
        .setSmallIcon(R.drawable.ic_service_notif)
        .setContentIntent(pendingIntent);

    return mBuilder.build();
  }

  @Override public void onDestroy() {
    super.onDestroy();
    Log.i(LOG_TAG, "In onDestroy");
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return mBinder;
  }

  private void testNetwork() {
    // Test network in Doze Mode
    Thread checkThread = new Thread(new Runnable() {
      @Override public void run() {
        while (serviceRunning) {
          boolean isConnected =
              MainActivity.isURLReachable(ForegroundService.this, "https://www.amazon.ca/");

          Log.d(Constants.TAG,
              "isConnected: " + isConnected + ", time: " + System.currentTimeMillis());

          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
    if (!checkThread.isAlive()){
      checkThread.start();
    }
  }

  public class LocalBinder extends Binder {
    public ForegroundService getService() {
      return ForegroundService.this;
    }
  }

}