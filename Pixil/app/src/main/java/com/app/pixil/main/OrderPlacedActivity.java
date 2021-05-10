package com.app.pixil.main;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderPlacedActivity extends BaseActivity {

    Button homeButton;
    TextView orderIDBox;
    AVLoadingIndicatorView progressBar;
    String price;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        String orderID = getIntent().getStringExtra("order_id");
        if(orderID.length() == 0)((LinearLayout)findViewById(R.id.orderIDLayout)).setVisibility(View.GONE);

        price = getIntent().getStringExtra("price");

        orderIDBox = (TextView) findViewById(R.id.orderID);
        orderIDBox.setText(orderID);

        Commons.selectedImages.clear();
        Commons.orderedImages.clear();
        Commons.imageStyles.clear();

        homeButton = (Button)findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        qpayAuth();

    }

    @Override
    public void onBackPressed() {

    }

    String accessToken;

    private void qpayAuth(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post("https://sandbox.qpay.mn/v1/auth/token")
                .addBodyParameter("client_id", "qpay_test")
                .addBodyParameter("client_secret", "1234")
                .addBodyParameter("grant_type", "client")
                .addBodyParameter("refresh_token", "")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE1!!!", response.toString());
                        // do anything with response

                        try {
                            accessToken = response.getString("access_token");
                            if(accessToken != null){
                                qpayBill(accessToken);
                            }else {
                                progressBar.setVisibility(View.GONE);
                                showToast(getString(R.string.serverissue));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            showToast(getString(R.string.serverissue));
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        showToast(getString(R.string.serverissue));
                    }
                });

    }

    private void qpayBill(String token){

        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("id", "CUST_001");
            jsonObj.put("register_no","ddf");
            jsonObj.put("name","Central Branch");
            jsonObj.put("email","info@info.mn");
            jsonObj.put("phone_number","99888899");
            jsonObj.put("note","davaa");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post("https://sandbox.qpay.mn/v1/bill/create")
                .addBodyParameter("template_id", "TEST_INVOICE")
                .addBodyParameter("merchant_id", "TEST_MERCHANT")
                .addBodyParameter("branch_id", "1")
                .addBodyParameter("pos_id", "1")
                .addBodyParameter("receiver", jsonObj.toString())
                .addBodyParameter("bill_no", "165465112kjh;kljlklj;kl3212121")
                .addBodyParameter("date", "2019-11-22 14:30")
                .addBodyParameter("description", "arvfscvfgcs")
                .addBodyParameter("amount", price)
                .addBodyParameter("btuk_code", "")
                .addBodyParameter("vat_flag", "0")
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE2!!!", response.toString());
                        // do anything with response
                        progressBar.setVisibility(View.GONE);
                        try {
                            String payment_id = response.getString("payment_id");
                            String qPay_QRcode = response.getString("qPay_QRcode");
                            String qPay_url = response.getString("qPay_url");
                            String qPay_QRimage = response.getString("qPay_QRimage");
                            JSONArray qPay_deeplink = response.getJSONArray("qPay_deeplink");
                            for(int i=0;i<qPay_deeplink.length(); i++){
                                JSONObject jsonObj = qPay_deeplink.getJSONObject(i);
                                String name = jsonObj.getString("name");
                                String link = jsonObj.getString("link");
                            }
                            if(payment_id != null){
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qPay_url + "?" + qPay_deeplink.getJSONObject(0).getString("link"))));
//                                qpayCheck(payment_id);
                            }else showToast(getString(R.string.serverissue));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast(getString(R.string.serverissue));
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        showToast(getString(R.string.serverissue));
                    }
                });

    }

    private void qpayCheck(String paymentId){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.get("https://sandbox.qpay.mn/v1/payment/check/" + paymentId)
                .addHeaders("Authorization", "Bearer " + accessToken)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE!!!", response.toString());
                        // do anything with response
                        progressBar.setVisibility(View.GONE);
                        try {
                            String name = response.getString("name");
                            String message = response.getString("message");
                            if(message != null){
                                showToast(message);
                            }else {
                                showToast(getString(R.string.serverissue));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast(getString(R.string.serverissue));
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        showToast(getString(R.string.serverissue));
                    }
                });

    }


}









































