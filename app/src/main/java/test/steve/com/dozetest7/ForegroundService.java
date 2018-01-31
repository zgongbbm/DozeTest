package test.steve.com.dozetest7;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ForegroundService extends JobService {
  private static final String LOG_TAG = "ForegroundService";

  public static boolean serviceRunning = false;

  @Override public void onCreate() {
    super.onCreate();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
      serviceRunning = true;
      Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
      Log.i(LOG_TAG, "Received Start Foreground Intent ");

      Intent notificationIntent = new Intent(this, MainActivity.class);
      notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

      RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.notification);

      // And now, building and attaching the Play button.
      Intent buttonPlayIntent = new Intent(this, NotificationPlayButtonHandler.class);
      buttonPlayIntent.putExtra("action", "togglePause");

      PendingIntent buttonPlayPendingIntent =
          pendingIntent.getBroadcast(this, 0, buttonPlayIntent, 0);
      notificationView.setOnClickPendingIntent(R.id.notification_button_play,
          buttonPlayPendingIntent);

      // And now, building and attaching the Skip button.
      Intent buttonSkipIntent = new Intent(this, NotificationSkipButtonHandler.class);
      buttonSkipIntent.putExtra("action", "skip");

      PendingIntent buttonSkipPendingIntent =
          pendingIntent.getBroadcast(this, 0, buttonSkipIntent, 0);
      notificationView.setOnClickPendingIntent(R.id.notification_button_skip,
          buttonSkipPendingIntent);

      // And now, building and attaching the Skip button.
      Intent buttonPrevIntent = new Intent(this, NotificationPrevButtonHandler.class);
      buttonPrevIntent.putExtra("action", "prev");

      PendingIntent buttonPrevPendingIntent =
          pendingIntent.getBroadcast(this, 0, buttonPrevIntent, 0);
      notificationView.setOnClickPendingIntent(R.id.notification_button_prev,
          buttonPrevPendingIntent);

      // And now, building and attaching the Close button.
      Intent buttonCloseIntent = new Intent(this, NotificationCloseButtonHandler.class);
      buttonCloseIntent.putExtra("action", "close");

      PendingIntent buttonClosePendingIntent =
          pendingIntent.getBroadcast(this, 0, buttonCloseIntent, 0);
      notificationView.setOnClickPendingIntent(R.id.notification_button_close,
          buttonClosePendingIntent);

      Notification notification =
          new NotificationCompat.Builder(this).setContentTitle("nkDroid Music Player")
              .setTicker("nkDroid Music Player")
              .setContentText("nkDroid Music")
              .setSmallIcon(R.mipmap.ic_launcher)
              .setContent(notificationView)
              .setOngoing(true)
              .build();

      startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

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

  @Override public void onDestroy() {
    super.onDestroy();
    Log.i(LOG_TAG, "In onDestroy");
  }

  @Override public boolean onStartJob(JobParameters params) {
    return false;
  }

  @Override public boolean onStopJob(JobParameters params) {
    return false;
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

  /**
   * Called when user clicks the "play/pause" button on the on-going system Notification.
   */
  public static class NotificationPlayButtonHandler extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(context, "Play Clicked", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Called when user clicks the "skip" button on the on-going system Notification.
   */
  public static class NotificationSkipButtonHandler extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(context, "Next Clicked", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Called when user clicks the "previous" button on the on-going system Notification.
   */
  public static class NotificationPrevButtonHandler extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(context, "Previous Clicked", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Called when user clicks the "close" button on the on-going system Notification.
   */
  public static class NotificationCloseButtonHandler extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(context, "Close Clicked", Toast.LENGTH_SHORT).show();
    }
  }
}