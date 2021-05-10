package com.app.pixil.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.ReqConst;
import com.app.pixil.models.Address;
import com.app.pixil.models.User;
import com.app.pixil.preference.Preference;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

public class AddShippingAddressActivity extends BaseActivity {

    EditText fullNameBox, phoneBox, addressBox, districtBox, cityBox, zipcodeBox, provinceBox;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shipping_address);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        fullNameBox = (EditText)findViewById(R.id.edt_name);
        phoneBox = (EditText)findViewById(R.id.edt_phone);
        addressBox = (EditText)findViewById(R.id.edt_address);
        districtBox = (EditText)findViewById(R.id.edt_district);
        cityBox = (EditText)findViewById(R.id.edt_city);
        zipcodeBox = (EditText)findViewById(R.id.edt_zipcode);
        provinceBox = (EditText)findViewById(R.id.edt_province);

        fullNameBox.setText(Commons.thisUser.getFirstName() + " " + Commons.thisUser.getLastName());
        phoneBox.setText(Commons.thisUser.getPhoneNumber());

        setupUI(findViewById(R.id.activity), this);
    }

    public void submit(View view){
        if(fullNameBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterfullname));
            return;
        }

        if(phoneBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterphonenumber));
            return;
        }

        if(addressBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enteraddress));
            return;
        }

        if(districtBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterdistrict));
            return;
        }
                                                                                                                            
        if(cityBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.entercityname));
            return;
        }

        if(zipcodeBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enterzipcode));
            return;
        }

        if(provinceBox.getText().toString().trim().length() == 0) {
            showToast(getString(R.string.enterprovince));
            return;
        }

        Address address = new Address();
        address.setAddress(addressBox.getText().toString().trim());
        address.setDistrict(districtBox.getText().toString().trim());
        address.setCity(cityBox.getText().toString().trim());
        address.setZipcode(zipcodeBox.getText().toString().trim());
        address.setProvince(provinceBox.getText().toString().trim());
        address.setFullName(fullNameBox.getText().toString().trim());
        address.setPhoneNumber(phoneBox.getText().toString().trim());

        Commons.address = address;
//        if(!Commons.addressList.contains(address)){
//            Commons.addressList.add(address);
//        }
//
//        SharedPreferences shref;
//        SharedPreferences.Editor editor;
//        shref = getSharedPreferences("PREF_ADDRESSLIST", Context.MODE_PRIVATE);
//
//        Gson gson = new Gson();
//        String json = gson.toJson(Commons.addressList);
//
//        editor = shref.edit();
//        editor.remove("addrList").commit();
//        editor.putString("addrList", json);
//        editor.commit();

        progressBar.setVisibility(View.VISIBLE);

        AndroidNetworking.post(ReqConst.SERVER_URL + "saveShippingAddress")
                .addBodyParameter("userId", String.valueOf(Commons.thisUser.getIdx()))
                .addBodyParameter("address", address.getAddress())
                .addBodyParameter("district", address.getDistrict())
                .addBodyParameter("city", address.getCity())
                .addBodyParameter("zip_code", address.getZipcode())
                .addBodyParameter("province", address.getProvince())
                .addBodyParameter("authorization", "pixil.mn")
                .setTag("save_shipping_address")
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
                                showToast(getString(R.string.saved));
                                finish();
                            }else if(result.equals("1")){
                                showToast(getString(R.string.serverissue));
                            }else if(result.equals("2")){
                                showToast(getString(R.string.existing));
                            }else {
                                showToast(getString(R.string.serverissue));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                        finish();
                    }
                });

    }

    public void back(View view){
        onBackPressed();
    }

}





























