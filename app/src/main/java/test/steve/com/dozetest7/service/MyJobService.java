package test.steve.com.dozetest7.service;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import test.steve.com.dozetest7.Constants;
import test.steve.com.dozetest7.ForegroundService;

/**
 * Created by zhegong on 01/02/18.
 */

public class MyJobService extends JobService {

  private static String TAG = "MyJobService";

  private PowerManager powerManager;

  private boolean jobRunning = false;

  @Override public boolean onStartJob(JobParameters job) {
    jobRunning = true;
    powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

    Log.d(TAG, "onStartJob...");
    new Thread(new Runnable() {
      @Override public void run() {
        while (jobRunning) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          Log.d(TAG, "isDeviceIdleMode: "
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
              Intent startIntent = new Intent(MyJobService.this, ForegroundService.class);
              startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
              startService(startIntent);
            }
          } else {
            //Intent stopIntent = new Intent(MyJobService.this, ForegroundService.class);
            //stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
            //startService(stopIntent);
          }
        }
      }
    }).start();
    return false;
  }

  @Override public boolean onStopJob(JobParameters job) {
    Log.d(TAG, "onStopJob...");
    jobRunning = false;
    return false;
  }
}
