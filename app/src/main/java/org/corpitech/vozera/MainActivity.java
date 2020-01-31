package org.corpitech.vozera;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.corpitech.vozera.vision.FrameProcessingActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        findViewById(R.id.launch_btn).setOnClickListener(v -> {
            final Intent intent = new Intent(MainActivity.this, FrameProcessingActivity.class);
            startActivity(intent);
        });
    }


}
