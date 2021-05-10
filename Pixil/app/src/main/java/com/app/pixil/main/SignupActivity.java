package com.app.pixil.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import static com.app.pixil.main.SigninActivity.isValidEmail;

public class SignupActivity extends BaseActivity {

    TextView termsButton, privacyButton;
    AVLoadingIndicatorView progressBar;
    EditText firstNameBox, lastNameBox, emailBox, passwordBox, phoneBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        termsButton = (TextView)findViewById(R.id.termsButton);
        privacyButton = (TextView)findViewById(R.id.privacyButton);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        firstNameBox = (EditText)findViewById(R.id.edt_firstname);
        lastNameBox = (EditText)findViewById(R.id.edt_lastname);
        phoneBox = (EditText)findViewById(R.id.edt_phone);
        emailBox = (EditText)findViewById(R.id.edt_email);
        passwordBox = (EditText)findViewById(R.id.edt_password);

        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(intent);
            }
        });

        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
                startActivity(intent);
            }
        });

        setupUI(findViewById(R.id.activity), this);

    }

    public void signup(View view){
        if(firstNameBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterfirstname));
            return;
        }
        if(lastNameBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterlastname));
            return;
        }
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
        if(phoneBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterphonenumber));
            return;
        }
        if(!isValidCellPhone(phoneBox.getText().toString().trim())){
            showToast(getString(R.string.invalidphonenumber));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "signup")
                .addBodyParameter("firstName", firstNameBox.getText().toString())
                .addBodyParameter("lastName", lastNameBox.getText().toString())
                .addBodyParameter("emailAddress", emailBox.getText().toString())
                .addBodyParameter("userPassword", passwordBox.getText().toString())
                .addBodyParameter("phoneNum", phoneBox.getText().toString())
                .addBodyParameter("authorization", "pixil.mn")
                .setTag("signup")
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
                                User user = new User();
                                user.setIdx(response.getInt("user_id"));
                                user.setFirstName(firstNameBox.getText().toString().trim());
                                user.setLastName(lastNameBox.getText().toString().trim());
                                user.setEmail(emailBox.getText().toString().trim());
                                user.setPhoneNumber(phoneBox.getText().toString().trim());
                                Commons.thisUser = user;
                                Preference.getInstance().put(getApplicationContext(), "email", user.getEmail());
                                Preference.getInstance().put(getApplicationContext(), "password", passwordBox.getText().toString());
                                Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class);
                                startActivity(intent);
                            }else if(result.equals("1")){
                                showToast(getString(R.string.existingemail));
                            }else {
                                showToast(getString(R.string.registrationfailed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast(getString(R.string.invalidregistration));
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        showToast(getString(R.string.invalidregistration));
                    }
                });

    }

    public void toSignin(View view){
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

}































