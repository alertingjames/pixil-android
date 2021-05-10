package com.app.pixil.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.app.pixil.preference.Preference;
import com.iamhabib.easy_preference.EasyPreference;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

public class PromoCodeActivity extends BaseActivity {

    EditText codeBox;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_code);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        codeBox = (EditText) findViewById(R.id.codeBox);

        setupUI(findViewById(R.id.activity), this);

    }

    public void back(View view){
        onBackPressed();
    }

    public void submitCode(View view){

        if(codeBox.getText().toString().trim().length() == 0){
            showToast(getString(R.string.enter_code));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "handlePromoCode")
                .addBodyParameter("promocode", codeBox.getText().toString().trim())
                .addBodyParameter("authorization", "pixil.mn")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE!!!", response.toString());
                        // do anything with response
                        progressBar.setVisibility(View.GONE);
//                        showAlertDialogForDiscount(30);
//                        return;
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                JSONObject jsonObj = response.getJSONObject("data");
                                int discount = jsonObj.getInt("off");
                                EasyPreference.with(getApplicationContext()).addString("discount", String.valueOf(discount)).save();
                                showAlertDialogForDiscount(discount);
                            }else if(result.equals("1")){
                                showToast(getString(R.string.invalid_promocode));
                            }else if(result.equals("2")){
                                showToast(getString(R.string.expired));
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
                        showToast(getString(R.string.serverissue));
                    }
                });

    }

    public void showAlertDialogForDiscount(int discount){
        AlertDialog.Builder builder = new AlertDialog.Builder(PromoCodeActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_promo, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();

        TextView textView = (TextView)view.findViewById(R.id.text);
        textView.setText(getString(R.string.yourpromecodeallset) + "\n" + getString(R.string.youwillget) + " " + String.valueOf(discount) + "% " + getString(R.string.percentoffyourorder));
        TextView okButton = (TextView)view.findViewById(R.id.btn_ok);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = (int) (displayWidth * 0.8f);
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
    }

}







































