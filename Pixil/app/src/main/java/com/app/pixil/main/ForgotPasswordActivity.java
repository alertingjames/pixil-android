package com.app.pixil.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.ReqConst;
import com.app.pixil.models.User;
import com.app.pixil.preference.Preference;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends BaseActivity {

    EditText emailBox;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        emailBox = (EditText)findViewById(R.id.edt_email);

        setupUI(findViewById(R.id.activity), this);
    }

    public void back(View view){
        onBackPressed();
    }

    public void submit(View view){
        if(emailBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enteremail));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "forgotPassword")
                .addBodyParameter("emailAddress", emailBox.getText().toString())
                .addBodyParameter("authorization", "pixil.mn")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE!!!", response.toString());
                        // do anything with response
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                                startActivity(intent);
                                finish();
                            }else if(result.equals("1")){
                                showToast(getString(R.string.noexistingemail));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast(getString(R.string.serverissue));
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        showToast(getString(R.string.serverissue));
                    }
                });
    }

}
