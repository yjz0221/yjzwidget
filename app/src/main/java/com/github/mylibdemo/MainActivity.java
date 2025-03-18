package com.github.mylibdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.github.yjz.widget.button.DotsLoadingButton;
import com.github.yjz.widget.popwindow.YjzWrapPopWindow;


public class MainActivity extends AppCompatActivity {

    private Button btnRequest;
    private DotsLoadingButton btnDotsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRequest = findViewById(R.id.btnRequest);
        btnDotsLoading = findViewById(R.id.btnDotsLoading);

        btnDotsLoading.loading();

        findViewById(R.id.btnRequest).setOnClickListener(v -> showPopBottom());
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