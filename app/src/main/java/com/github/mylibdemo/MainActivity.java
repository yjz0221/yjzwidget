package com.github.mylibdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.github.yjz.widget.button.RotateLoadingButton;
import com.github.yjz.widget.button.DotsLoadingButton;
import com.github.yjz.widget.popwindow.YjzWrapPopWindow;


public class MainActivity extends AppCompatActivity {

    private Button btnRequest;
    private RotateLoadingButton btnProgress;
    private DotsLoadingButton btnDotsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRequest = findViewById(R.id.btnRequest);
        btnProgress = findViewById(R.id.btnProgress);
        btnDotsLoading = findViewById(R.id.btnDotsLoading);

        btnDotsLoading.loading();

        findViewById(R.id.btnRequest).setOnClickListener(v -> showPopBottom());

        findViewById(R.id.btnNormal).setOnClickListener(v -> btnProgress.normal());

        findViewById(R.id.btnLoading).setOnClickListener(v -> btnProgress.loading());

        findViewById(R.id.btnFinish).setOnClickListener(v -> btnProgress.finish());

        findViewById(R.id.btnError).setOnClickListener(v -> btnProgress.error());
    }


    private void showPopBottom(){
        YjzWrapPopWindow popWindow = new YjzWrapPopWindow.PopupWindowBuilder(this)
                .setView(R.layout.pop_test)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .create();

        popWindow.showAsDropDown(btnRequest,0,10);
    }

}