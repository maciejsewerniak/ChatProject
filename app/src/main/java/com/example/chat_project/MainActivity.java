package com.example.chat_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.chat_project.activities.LyricsMaster;
import com.example.chat_project.activities.LyricsSlave;

public class MainActivity extends AppCompatActivity {

    public void beServer(View view) {
        Intent serverActivityIntent = new Intent(this, LyricsMaster.class);
        this.startActivity(serverActivityIntent);
    }

    public void beClient(View view) {
        Intent clientActivityIntent = new Intent(this, LyricsSlave.class);
        this.startActivity(clientActivityIntent);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
