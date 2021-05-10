package com.app.pixil.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;

public class TermsActivity extends BaseActivity {

    TextView title, text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        title = (TextView)findViewById(R.id.title) ;
        text = (TextView)findViewById(R.id.text);

    }

    public void back(View view){
        onBackPressed();
    }
}
