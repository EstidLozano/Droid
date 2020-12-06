package com.blieve.droid.component;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import com.blieve.droid.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final WindowInsetsController insetsController = getWindow().getInsetsController();
        final int inset = WindowInsets.Type.statusBars();
        insetsController.hide(inset);
    }

}