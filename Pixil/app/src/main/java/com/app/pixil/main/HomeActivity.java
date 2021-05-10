package com.app.pixil.main;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.pixil.R;
import com.app.pixil.adapters.HomePictureListAdapter;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.classes.HeightWrappingViewPager;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.ReqConst;
import com.app.pixil.models.Image;
import com.app.pixil.preference.Preference;
import com.appunite.appunitevideoplayer.PlayerActivity;
import com.bumptech.glide.Glide;
import com.rd.PageIndicatorView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import softpro.naseemali.ShapedNavigationView;

public class HomeActivity extends BaseActivity{

    DrawerLayout drawer;
    ShapedNavigationView navigationView;
    HeightWrappingViewPager pager;
    PageIndicatorView pageIndicatorView;
    ArrayList<Integer> pictures = new ArrayList<>();
    HomePictureListAdapter adapter = new HomePictureListAdapter(this);

    ImageView videoThumb;
    ImageButton playButton;
    AVLoadingIndicatorView loadingIndicatorView;
    String phone_status = "";

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.ACCESS_FINE_LOCATION,
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.SET_TIME,
            android.Manifest.permission.READ_PHONE_STATE,
//            android.Manifest.permission.RECORD_AUDIO,
//            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.WAKE_LOCK,
//            android.Manifest.permission.READ_CALENDAR,
//            android.Manifest.permission.WRITE_CALENDAR,
//            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_CONTACTS,
//            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
//            android.Manifest.permission.CALL_PHONE,
//            android.Manifest.permission.CALL_PRIVILEGED,
            android.Manifest.permission.SYSTEM_ALERT_WINDOW,
//            android.Manifest.permission.LOCATION_HARDWARE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        checkAllPermission();
        phone_status = Preference.getInstance().getValue (getApplicationContext(), "phone_status", "");

        loadingIndicatorView = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Log.d("Slide Offset+++", String.valueOf(slideOffset));
                if (slideOffset > 0.1){
                    toggleFullscreen(true);
                }else{
                    toggleFullscreen(false);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                toggleFullscreen(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                toggleFullscreen(false);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
            }
        });

        navigationView = (ShapedNavigationView) findViewById(R.id.navigation_view);

        pager = (HeightWrappingViewPager)findViewById(R.id.viewPager);
        pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);

        videoThumb = (ImageView)findViewById(R.id.videoThumb);
        playButton = (ImageButton) findViewById(R.id.playButton);

        videoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

//        getAdDetail();
        setAdPics();

//        deleteCache(getApplicationContext());
//        clearApplicationData();

    }

    private void setAdPics(){

        pictures.add(R.drawable.insta_1);
        pictures.add(R.drawable.insta_2);
        pictures.add(R.drawable.insta_3);
        pictures.add(R.drawable.insta_4);
        pictures.add(R.drawable.insta_5);
        pictures.add(R.drawable.insta_6);
        pictures.add(R.drawable.insta_7);
        pictures.add(R.drawable.insta_8);
        pictures.add(R.drawable.insta_9);

        adapter.setDatas(pictures);
        pager.setAdapter(adapter);

    }

    private void toggleFullscreen(boolean fullscreen)
    {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen)
        {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        else
        {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }

    public void openMenu(View view){
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    public void toMyOrders(View view){
        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(intent);
        }else {
//            if(phone_status.length() == 0){
//                Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class);
//                startActivity(intent);
//                return;
//            }
            Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
            startActivity(intent);
        }
        overridePendingTransition(0,0);
        closeMenu();
    }

    public void toContactUs(View view){
        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(intent);
        }else {
//            if(phone_status.length() == 0){
//                Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class);
//                startActivity(intent);
//                return;
//            }
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        }
        overridePendingTransition(0,0);
        closeMenu();
    }

    public void toQuestions(View view){
        Intent intent = new Intent(getApplicationContext(), QuestionsActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        closeMenu();
    }

    public void toLegal(View view){
        showAlertDialogForLegal();
        closeMenu();
    }

    public void toSettings(View view){
        showAlertDialogForSettings();
        closeMenu();
    }

    public void toAddPromo(View view){
        Intent intent = new Intent(getApplicationContext(), PromoCodeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        closeMenu();
    }

    private void closeMenu(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
    }

    public void letsOrder(View view){
        if(Commons.thisUser == null){
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            startActivity(intent);
        }else {
//            if(phone_status.length() == 0){
//                Intent intent = new Intent(getApplicationContext(), PhoneVerifyActivity.class);         /////////////////////////////////////////////////
////                Intent intent = new Intent(getApplicationContext(), SelectPhotosActivity.class);
//                startActivity(intent);
//                return;
//            }
            Intent intent = new Intent(getApplicationContext(), SelectPhotosActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Commons.selectedImagePaths.clear();
        for(Image image: Commons.selectedImages){
            Commons.selectedImagePaths.add(image.getPath());
        }
    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showAlertDialogForLegal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.legal_menu_layout, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        wmlp.x = 0;   //x position
        wmlp.y = 0;   //y position

        dialog.show();

        TextView termsButton = (TextView)view.findViewById(R.id.termsButton);
        TextView privacyButton = (TextView)view.findViewById(R.id.privacyButton);
        TextView cancelButton = (TextView)view.findViewById(R.id.cancelButton);

        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
                startActivity(intent);

                dialog.dismiss();
            }
        });

        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
                startActivity(intent);

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
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

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth * 1.0f);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    private void getAdDetail(){
        loadingIndicatorView.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getAdDetails")
                .addBodyParameter("authorization", "pixil.mn")
                .setTag("getAdDetails")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        loadingIndicatorView.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            Log.d("AD detail resp!!!", response.toString());
                            if(result.equals("0")){
                                String videoUrl = response.getString("videoUrl");
                                String imageUrl = response.getString("imageUrl");
                                Glide.with(getApplicationContext())
                                        .load(imageUrl)
                                        .into(videoThumb);
                                videoThumb.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(videoUrl.length() > 0){

                                            startActivity(PlayerActivity.getVideoPlayerIntent(getApplicationContext(),
                                                    videoUrl,
                                                    ""));

//                                            Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
//                                            intent.putExtra("url", videoUrl);
//                                            startActivity(intent);
//                                            overridePendingTransition(0,0);
                                        }
                                    }
                                });

                                playButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(videoUrl.length() > 0){

                                            startActivity(PlayerActivity.getVideoPlayerIntent(getApplicationContext(),
                                                    videoUrl,
                                                    ""));
                                        }
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    public void showAlertDialogForSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.settings_alert_layout, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        wmlp.x = 15;   //x position
        wmlp.y = 15;   //y position

        dialog.show();

        TextView enButton = (TextView)view.findViewById(R.id.enButton);
        TextView mnButton = (TextView)view.findViewById(R.id.mnButton);
        TextView cancelButton = (TextView)view.findViewById(R.id.cancelButton);

        enButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preference.getInstance().put(getApplicationContext(), "lang", "en");
                Commons.lang = "en";
                setLanguage("en");

                dialog.dismiss();
            }
        });

        mnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preference.getInstance().put(getApplicationContext(), "lang", "mn");
                Commons.lang = "mn";
                setLanguage("mn");

                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
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

        // Set alert dialog width equal to screen width 80%
        int dialogWindowWidth = (int) (displayWidth - 30);
        // Set alert dialog height equal to screen height 80%
        //    int dialogWindowHeight = (int) (displayHeight * 0.8f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //      layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        dialog.getWindow().setAttributes(layoutParams);
    }

}























