package com.github.mylibdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.yjz.widget.button.DotsLoadingButton;
import com.github.yjz.widget.button.YjzTextLoadingButton;
import com.github.yjz.widget.button.YjzLoadingButton;
import com.github.yjz.widget.popwindow.YjzWrapPopWindow;


public class MainActivity extends AppCompatActivity {

    private Button btnRequest;
    private DotsLoadingButton btnDotsLoading;
    private YjzLoadingButton loadingButton;
    private YjzTextLoadingButton btnLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRequest = findViewById(R.id.btnRequest);
        btnDotsLoading = findViewById(R.id.btnDotsLoading);
        loadingButton = findViewById(R.id.my_loading_button);
        btnLoading = findViewById(R.id.btnLoading);

        btnLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnLoading.isLoading()) {
                    btnLoading.startLoading("登录中");
                    btnLoading.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnLoading.stopLoading("登录成功!");
                        }
                    }, 2000);
                }
            }
        });

        btnDotsLoading.loading();

        loadingButton.setOnClickListener(v -> {
            if (loadingButton.isLoading()) {
                return;
            }

            loadingButton.loading(true);
            loadingButton.postDelayed(() -> loadingButton.normal(), 3000);
        });

        findViewById(R.id.btnRequest).setOnClickListener(v -> showPopBottom());
    }


    private void showPopBottom() {
        YjzWrapPopWindow popWindow = new YjzWrapPopWindow.PopupWindowBuilder(this)
                .setView(R.layout.pop_test)
                .setFocusable(true)
                .setOutsideTouchable(true)
                .create();

        popWindow.showAsDropDown(btnRequest, 0, 10);
    }

}