package com.app.pixil.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

public class SigninActivity extends BaseActivity {

    AVLoadingIndicatorView progressBar;
    EditText emailBox, passwordBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        emailBox = (EditText)findViewById(R.id.edt_email);
        passwordBox = (EditText)findViewById(R.id.edt_password);

        setupUI(findViewById(R.id.activity), this);

    }

    public void signin(View view){
        if(emailBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enteremail));
            return;
        }
        if(!isValidEmail(emailBox.getText().toString().trim())){
            showToast(getString(R.string.invalidemail));
            return;
        }
        if(passwordBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterpassword));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "signin")
                .addBodyParameter("emailAddress", emailBox.getText().toString())
                .addBodyParameter("userPassword", passwordBox.getText().toString())
                .addBodyParameter("authorization", "pixil.mn")
                .setTag("signin")
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
                                JSONObject userObj = response.getJSONObject("user_data");
                                User user = new User();
                                user.setIdx(userObj.getInt("u_id"));
                                user.setFirstName(userObj.getString("u_firstname"));
                                user.setLastName(userObj.getString("u_lastname"));
                                user.setEmail(userObj.getString("u_email"));
                                user.setPhoneNumber(userObj.getString("u_phone"));
                                Commons.thisUser = user;
                                Preference.getInstance().put(getApplicationContext(), "email", user.getEmail());
                                Preference.getInstance().put(getApplicationContext(), "password", passwordBox.getText().toString());
                                String phone_status = Preference.getInstance().getValue(getApplicationContext(), "phone_status", "");
//                                if(phone_status.equals("verified")){
//                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                                    startActivity(intent);
                                    showToast(getString(R.string.signedin));
                                    finish();
//                                }
//                                else {
//                                    Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class); /////////////////////////////////////////////////////////////////
//                                    startActivity(intent);
//                                    finish();
//                                }
                            }else {
                                showToast(getString(R.string.loginfailed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast(getString(R.string.invalidlogin));
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        showToast(getString(R.string.invalidlogin));
                    }
                });
    }

    public void toForgotPassword(View view){
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void toSignup(View view){
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

}
