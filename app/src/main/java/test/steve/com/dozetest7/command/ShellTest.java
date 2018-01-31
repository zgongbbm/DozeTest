package test.steve.com.dozetest7.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zhegong on 31/01/18.
 */

public class ShellTest {
  public static String executer() {

    try {
      Process process = Runtime.getRuntime().exec("/system/bin/ls");
      InputStreamReader reader = new InputStreamReader(process.getInputStream());
      BufferedReader bufferedReader = new BufferedReader(reader);
      int numRead;
      char[] buffer = new char[5000];
      StringBuilder commandOutput = new StringBuilder();
      while ((numRead = bufferedReader.read(buffer)) > 0) {
        commandOutput.append(buffer, 0, numRead);
      }
      bufferedReader.close();
      process.waitFor();

      return commandOutput.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

