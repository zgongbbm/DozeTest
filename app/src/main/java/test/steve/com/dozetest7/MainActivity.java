package test.steve.com.dozetest7;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import test.steve.com.dozetest7.command.ShellTest;

public class MainActivity extends Activity {

  private PowerManager powerManager;

  private Handler mHandler;

  @Override protected void onStart() {
    super.onStart();
    IntentFilter filter_system = new IntentFilter();
    filter_system.addAction(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED);
    filter_system.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
    filter_system.addAction("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
    registerReceiver(systemReceiver, filter_system);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
    mHandler = new Handler(getMainLooper());

    new Thread(new Runnable() {
      @Override public void run() {
        while (true) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Log.d(Constants.TAG, "isDeviceIdleMode: "
              + powerManager.isDeviceIdleMode()
              + ", time: "
              + System.currentTimeMillis());
          if (powerManager.isDeviceIdleMode()) {
            if (!ForegroundService.serviceRunning) {
              try {
                Thread.sleep(4 * 1000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
              startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
              startService(startIntent);
            }
          } else {
            //Intent stopIntent = new Intent(MainActivity.this, ForegroundService.class);
            //stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            //startService(stopIntent);
          }
        }
      }
    }).start();
  }

  static public boolean isURLReachable(Context context, String urlstring) {
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if (netInfo != null && netInfo.isConnected()) {
      try {
        URL url = new URL(urlstring);   // Change to "http://google.com" for www  test.
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
        urlc.setConnectTimeout(10 * 1000);          // 10 s.
        urlc.connect();
        if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
          Log.wtf("Connection", "Success !");
          return true;
        } else {
          return false;
        }
      } catch (MalformedURLException e1) {
        return false;
      } catch (IOException e) {
        return false;
      }
    }
    return false;
  }

  @Override protected void onStop() {
    super.onStop();
    unregisterReceiver(systemReceiver);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  private BroadcastReceiver systemReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {
      if (intent.getAction() == null) {
        return;
      }
      switch (intent.getAction()) {

        case PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED:
          Log.d(Constants.TAG, "ACTION_DEVICE_IDLE_MODE_CHANGED: isDeviceIdleMode: "
              + powerManager.isDeviceIdleMode()
              + ", time: "
              + System.currentTimeMillis());
          if (powerManager.isDeviceIdleMode()) {
            Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
            startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            startService(startIntent);
          } else {
            Intent stopIntent = new Intent(MainActivity.this, ForegroundService.class);
            stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            startService(stopIntent);
          }
          break;
        case PowerManager.ACTION_POWER_SAVE_MODE_CHANGED:
          Log.d(Constants.TAG, "ACTION_POWER_SAVE_MODE_CHANGED: isPowerSaveMode: "
              + powerManager.isPowerSaveMode()
              + ", time: "
              + System.currentTimeMillis());
          break;
        case "android.os.action.POWER_SAVE_WHITELIST_CHANGED":
          Log.d(Constants.TAG, "android.os.action.POWER_SAVE_WHITELIST_CHANGED");
          break;
      }
    }
  };
}
