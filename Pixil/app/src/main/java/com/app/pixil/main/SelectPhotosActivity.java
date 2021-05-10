package com.app.pixil.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.pixil.R;
import com.app.pixil.adapters.PictureListAdapter;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.classes.CustomGridView;
import com.app.pixil.commons.Commons;
import com.app.pixil.models.GalleryImage;
import com.app.pixil.models.Image;
import com.app.pixil.preference.Preference;

import java.io.File;
import java.util.ArrayList;

import softpro.naseemali.ShapedNavigationView;

public class SelectPhotosActivity extends BaseActivity {

    DrawerLayout drawer;
    ShapedNavigationView navigationView;
    TextView btn_all, btn_facebook, btn_instagram, btn_google;
    LinearLayout cloudFrame;
    private static final int REQUEST_PERMISSIONS = 100;

    public static ArrayList<GalleryImage> al_images = new ArrayList<>();
    boolean boolean_folder;
    CustomGridView list;
    public TextView textView, textView0;
    public static ArrayList<Image> allFiles = new ArrayList<>();
    PictureListAdapter adapter = new PictureListAdapter(this);

    LinearLayout googleButton, instagramButton, facebookButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photos);

        Commons.selectPhotosActivity = this;

        textView = (TextView)findViewById(R.id.text);
        textView0 = (TextView)findViewById(R.id.text0);

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

        btn_all = (TextView) findViewById(R.id.all);
        btn_facebook = (TextView)findViewById(R.id.facebook);
        btn_instagram = (TextView)findViewById(R.id.instagram);
        btn_google = (TextView)findViewById(R.id.google);

        cloudFrame = (LinearLayout)findViewById(R.id.cloudFrame);

        list = (CustomGridView)findViewById(R.id.list);

        selAllPhotos();

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(SelectPhotosActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(SelectPhotosActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(SelectPhotosActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            getGalleryImages();
        }

        googleButton = (LinearLayout)findViewById(R.id.btn_google);
        instagramButton = (LinearLayout)findViewById(R.id.btn_instagram);
        facebookButton = (LinearLayout)findViewById(R.id.btn_facebook);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SocialLoginActivity.class);
                startActivity(intent);
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SocialLoginActivity.class);
                startActivity(intent);
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SocialLoginActivity.class);
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.toStylePhotos)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.selectedImages.size() >= 3){
                    Intent intent = new Intent(getApplicationContext(), StylePhotosActivity.class);
                    startActivity(intent);
                }else{
                    showToast(getString(R.string.choose3photos));
                }
            }
        });

        if(Commons.selectedImages.size() == 0){
            textView.setText(getString(R.string.selectphotoscap));
            textView0.setText(getString(R.string.selectphotoscap2));
        }else{
            textView.setText(String.valueOf(Commons.selectedImages.size()) + " " + (Commons.selectedImages.size() == 1?"pixil":"pixils") +
                    " " + getString(R.string.are) + " ₮" + String.valueOf(Commons.selectedImages.size()%3==0?50000*Commons.selectedImages.size()/3:(int)50000/3 * Commons.selectedImages.size()));
            textView0.setText(
                    (Commons.selectedImages.size() < 3)? getString(R.string.select) + " " + String.valueOf(3 - Commons.selectedImages.size()) + " " + getString(R.string.more) : "");
        }


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

    private void resetButtons(){
        btn_all.setTextColor(getColor(R.color.gray));
        btn_facebook.setTextColor(getColor(R.color.gray));
        btn_instagram.setTextColor(getColor(R.color.gray));
        btn_google.setTextColor(getColor(R.color.gray));
    }

    public void allPhotos(View view){
        selAllPhotos();
    }

    private void selAllPhotos(){
        resetButtons();
        btn_all.setTextColor(getColor(R.color.colorPrimary));
        cloudFrame.setVisibility(View.VISIBLE);
    }

    public void facebookPhotos(View view){
        resetButtons();
        btn_facebook.setTextColor(getColor(R.color.colorPrimary));
        cloudFrame.setVisibility(View.GONE);
    }

    public void instagramPhotos(View view){
        resetButtons();
        btn_instagram.setTextColor(getColor(R.color.colorPrimary));
        cloudFrame.setVisibility(View.GONE);
    }

    public void googlePhotos(View view){
        resetButtons();
        btn_google.setTextColor(getColor(R.color.colorPrimary));
        cloudFrame.setVisibility(View.GONE);
    }

    private void getGalleryImages(){
        adapter.setDatas(images());
        list.setAdapter(adapter);
    }

    public ArrayList<Image> images() {
        al_images.clear();
        allFiles.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            ArrayList<String> al_path = new ArrayList<>();
            al_path.add(absolutePathOfImage);
            GalleryImage obj_model = new GalleryImage();
            obj_model.setStr_folder(cursor.getString(column_index_folder_name));
            obj_model.setAl_imagepath(al_path);

            al_images.add(obj_model);

        }

        for (int i = 0; i < al_images.size(); i++) {
            Log.e("FOLDER", al_images.get(i).getStr_folder());
            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
                Image image = new Image();
                image.setFile(new File(al_images.get(i).getAl_imagepath().get(j)));
                image.setPath(al_images.get(i).getAl_imagepath().get(j));
                image.setCopyCount(1);
                image.setSelected(false);
                allFiles.add(image);

                Log.d("FILE SIZE 000 !!!", calculateFileSize(image.getPath()));
            }
        }

        return allFiles;
    }

    public String calculateFileSize(String filepath)
    {
        //String filepathstr=filepath.toString();
        File file = new File(filepath);

        long fileSizeInBytes = file.length();
        float fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        float fileSizeInMB = fileSizeInKB / 1024;

        String calString = Float.toString(fileSizeInMB);
        return calString;
    }

    public String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getGalleryImages();
                    } else {
                        Toast.makeText(SelectPhotosActivity.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Commons.selectedImages.clear();
    }

    public void back(View view){
        Commons.selectPhotosActivity = null;
        onBackPressed();
    }

    public void showAlertDialogForLegal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPhotosActivity.this);
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

    public void showAlertDialogForSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPhotosActivity.this);
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
































