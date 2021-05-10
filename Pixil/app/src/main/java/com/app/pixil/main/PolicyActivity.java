package com.app.pixil.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;

public class PolicyActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);



    }

    public void back(View view){
        onBackPressed();
    }
}
