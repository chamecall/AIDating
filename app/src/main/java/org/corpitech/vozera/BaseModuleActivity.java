package org.corpitech.vozera;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class BaseModuleActivity extends AppCompatActivity {

  protected HandlerThread mBackgroundThread;
  protected Handler mBackgroundHandler;


  @Override
  protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    startBackgroundThread();
  }

  protected void startBackgroundThread() {
    mBackgroundThread = new HandlerThread("ModuleActivity");
    mBackgroundThread.start();
    mBackgroundHandler = new Handler(mBackgroundThread.getLooper());


  }

  @Override
  protected void onDestroy() {
    stopBackgroundThread();
    super.onDestroy();
  }

  protected void stopBackgroundThread() {
    mBackgroundThread.quitSafely();
    try {
      mBackgroundThread.join();
      mBackgroundThread = null;
      mBackgroundHandler = null;
    } catch (InterruptedException e) {
      Log.e(Constants.TAG, "Error on stopping background thread", e);
    }
  }



  @UiThread
  protected void showErrorDialog(View.OnClickListener clickListener) {
    final View view = newErrorDialogView(this);
    final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Widget_AppCompat_Button_ButtonBar_AlertDialog)
        .setCancelable(false)
        .setView(view);
    final AlertDialog alertDialog = builder.show();
    view.setOnClickListener(v -> {
      clickListener.onClick(v);
      alertDialog.dismiss();
    });
  }

  public static View newErrorDialogView(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.error_dialog, null, false);
    return view;
  }

}
