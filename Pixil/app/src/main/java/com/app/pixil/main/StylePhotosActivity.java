package com.app.pixil.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.pixil.R;
import com.app.pixil.adapters.FramePictureListAdapter;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.ReqConst;
import com.app.pixil.models.Address;
import com.app.pixil.models.Image;
import com.app.pixil.models.ImageStyle;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class StylePhotosActivity extends BaseActivity {

    int[] frames = new int[]{R.drawable.modern, R.drawable.white, R.drawable.classic};
    int[] frames2 = new int[]{R.drawable.aa, R.drawable.bb, R.drawable.cc};
    String[] titles = new String[]{"modern", "white", "classic"};
    ArrayList<View> frameViews = new ArrayList<>();
    Button checkoutButton;
    public int selectedStyle = 0;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    FramePictureListAdapter adapter;
    LinearLayout addressListLayout;
    ListView addrListView;
    ImageView addrListCloseButton;
    TextView newAddressButton;

    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_photos);

        Commons.stylePhotosActivity = this;

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        recyclerView = (RecyclerView) findViewById(R.id.pictureList);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        getPhotoStyles();

        checkoutButton = (Button)findViewById(R.id.checkout);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogForCheckout();
            }
        });

        addrListView = (ListView)findViewById(R.id.list);
        addrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commons.address = Commons.addressList.get(position);
                closeAddressListLayout();
            }
        });
        addressListLayout = (LinearLayout)findViewById(R.id.addressListLayout);
        addrListCloseButton = (ImageView)findViewById(R.id.btn_close);
        newAddressButton = (TextView)findViewById(R.id.btn_new);
        addrListCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addressListLayout.getVisibility() == View.VISIBLE){
                    closeAddressListLayout();
                }
            }
        });
        newAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddShippingAddressActivity.class);
                startActivity(intent);
                closeAddressListLayout();
            }
        });
    }

    private void closeAddressListLayout(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
        addressListLayout.setAnimation(animation);
        addressListLayout.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getShippingAddressList();
    }

    private void initFrameList(){
        ((LinearLayout)findViewById(R.id.frames)).removeAllViews();
        Commons.imageStyles.clear();
        frameViews.clear();
        selectedStyle = 0;
        for(int i=0; i<frames.length; i++){
            ImageStyle imageStyle = new ImageStyle();
            imageStyle.setTitle(titles[i]);
            imageStyle.setFrameRes(frames2[i]);
            Commons.imageStyles.add(imageStyle);

            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_frame_list, null);
            ImageView picture = (ImageView) view.findViewById(R.id.frame_picture);
            TextView title = (TextView)view.findViewById(R.id.frame_title);
            View indicator = (View) view.findViewById(R.id.indicator);
            Picasso.with(getApplicationContext()).load(frames[i]).into(picture);
            title.setText(imageStyle.getTitle());
            if(i == 0){
                title.setTextColor(getColor(R.color.colorPrimary));
                indicator.setVisibility(View.VISIBLE);
            }else{
                title.setTextColor(getColor(R.color.text));
                indicator.setVisibility(View.INVISIBLE);
            }
            int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetFrameList();
                    title.setTextColor(getColor(R.color.colorPrimary));
                    indicator.setVisibility(View.VISIBLE);
                    selectedStyle = finalI;
                    loadSelectedImages(imageStyle);
//                    loadImages();
                }
            });
            frameViews.add(view);
            ((LinearLayout)findViewById(R.id.frames)).addView(view);
        }
    }

    public void loadImages(){
        adapter = new FramePictureListAdapter(getApplicationContext(), Commons.selectedImages);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void resetFrameList(){
        for(View view: frameViews){
            ((TextView)view.findViewById(R.id.frame_title)).setTextColor(getColor(R.color.text));
            ((View)view.findViewById(R.id.indicator)).setVisibility(View.INVISIBLE);
        }
    }

    public void loadSelectedImages(ImageStyle imageStyle){
        ((LinearLayout)findViewById(R.id.pictures)).removeAllViews();
        int i = 0;
        for(Image image:Commons.selectedImages){
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_frame_image, null);
            ImageView frame = (ImageView) view.findViewById(R.id.frame_picture);
            ImageView right = (ImageView) view.findViewById(R.id.right);
            ImageView bottom = (ImageView) view.findViewById(R.id.bottom);
            ImageView picture = (ImageView)view.findViewById(R.id.picture);
            FrameLayout pictureLayout = (FrameLayout)view.findViewById(R.id.picture_layout);
            LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout);
//            frame.setImageResource(Commons.imageStyles.get(selectedStyle).getFrameRes());
            Picasso.with(getApplicationContext()).load(Commons.imageStyles.get(selectedStyle).getFrame()).into(frame);
            Picasso.with(getApplicationContext()).load(Commons.imageStyles.get(selectedStyle).getRightSide()).into(right);
            Picasso.with(getApplicationContext()).load(Commons.imageStyles.get(selectedStyle).getBottomSide()).into(bottom);

            int frameW = frame.getLayoutParams().width;
            int frameH = frame.getLayoutParams().height;

            int marginLeft = (int) (frameW - frameW * imageStyle.getFrameWidth() / imageStyle.getPhotoWidth()) / 2;
            int marginRight = (int) (frameW - frameW * imageStyle.getFrameWidth() / imageStyle.getPhotoWidth()) / 2;
            int marginTop = (int) (frameH - frameH * imageStyle.getFrameHeight() / imageStyle.getPhotoHeight()) / 2;
            int marginBottom = (int) (frameH - frameH * imageStyle.getFrameHeight() / imageStyle.getPhotoHeight()) / 2;

            i++;

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
            layout.setLayoutParams(layoutParams);

            if(selectedStyle == 1) {
                layout.setBackgroundResource(R.drawable.black_stroke);
                layout.setPadding(3,3,3,3);
            }else{
                layout.setBackground(null);
                layout.setPadding(0,0,0,0);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            if(i == 1){
                params.setMargins(50,0, 0, 0);
            }else if(i == Commons.selectedImages.size()){
                params.setMargins(0,0, 50, 0);
            }

            view.setLayoutParams(params);

            Picasso.with(getApplicationContext())
                    .load(image.getFile())
                    .into(picture);
            image.setView(view);

            int index = Commons.selectedImages.indexOf(image);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlertDialogForEdit(index);
                }
            });
            ((LinearLayout)findViewById(R.id.pictures)).addView(view);

            Log.d("FILE SIZE 111 !!!", calculateFileSize(image.getPath()));

        }
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

    public void showAlertDialogForEdit(int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(StylePhotosActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.stylephotos_editmenu_layout, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        wmlp.x = 0;   //x position
        wmlp.y = 0;   //y position

        dialog.show();

        LinearLayout copyButton = (LinearLayout)view.findViewById(R.id.copyButton);
        LinearLayout editButton = (LinearLayout)view.findViewById(R.id.editButton);
        LinearLayout removeButton = (LinearLayout)view.findViewById(R.id.removeButton);
        LinearLayout cancelButton = (LinearLayout)view.findViewById(R.id.cancelButton);

        TextView minusButton = (TextView)view.findViewById(R.id.minusButton);
        TextView plusButton = (TextView)view.findViewById(R.id.plusButton);
        TextView countBox = (TextView)view.findViewById(R.id.countBox);

        countBox.setText(String.valueOf(Commons.selectedImages.get(index).getCopyCount()));

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.selectedImages.get(index).getCopyCount() > 1)
                    Commons.selectedImages.get(index).setCopyCount(Commons.selectedImages.get(index).getCopyCount() - 1);
                countBox.setText(String.valueOf(Commons.selectedImages.get(index).getCopyCount()));
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.selectedImages.get(index).setCopyCount(Commons.selectedImages.get(index).getCopyCount() + 1);
                countBox.setText(String.valueOf(Commons.selectedImages.get(index).getCopyCount()));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.image = Commons.selectedImages.get(index);
                Bitmap beforeBitmap = BitmapFactory.decodeFile(Commons.image.getFile().getPath());
                Log.e("111 Dimensions", beforeBitmap.getWidth() + "-" + beforeBitmap.getHeight());

                Intent intent = new Intent(getApplicationContext(), EditPhotoActivity.class);
                startActivity(intent);

                dialog.dismiss();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.selectedImages.size() <= 3){
                    showToast(getString(R.string.cantremoveanymore));
                    return;
                }
                Commons.selectedImages.remove(index);
                loadSelectedImages(Commons.imageStyles.get(selectedStyle));
//                loadImages();

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

    public void showAlertDialogForCheckout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(StylePhotosActivity.this);
        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.checkout_menu_layout, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        wmlp.x = 0;   //x position
        wmlp.y = 0;   //y position

        dialog.show();

        LinearLayout addShippingButton = (LinearLayout)view.findViewById(R.id.addShippingButton);
        Button confirmOrderButton = (Button)view.findViewById(R.id.confirmOrderButton);

        addShippingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.addressList.size() > 0){
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
                    addressListLayout.setAnimation(animation);
                    addressListLayout.setVisibility(View.VISIBLE);

                    ArrayList<String> addrList = new ArrayList<String>();
                    for(Address address:Commons.addressList){
                        addrList.add(address.getAddress() + ", " + address.getDistrict() + ", " + address.getCity() + ", " + address.getZipcode() + ", " + address.getProvince());
                    }
                    ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_addr, addrList);
                    addrListView.setAdapter( listAdapter );

                }else {
                    Intent intent = new Intent(getApplicationContext(), AddShippingAddressActivity.class);
                    startActivity(intent);
                }
                dialog.dismiss();
            }
        });

        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Commons.address == null){
                    showToast(getString(R.string.addshippingaddressplease));
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ConfirmOrderActivity.class);
                startActivity(intent);
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

    public void back(View view){
        Commons.stylePhotosActivity = null;
        onBackPressed();
    }

    private void getPhotoStyles(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getPhotoStyles")
                .addBodyParameter("authorization", "pixil.mn")
                .setTag("getPhotoStyles")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE!!!", response.toString());
                        // do anything with response
                        ((LinearLayout)findViewById(R.id.frames)).removeAllViews();
                        Commons.imageStyles.clear();
                        frameViews.clear();
                        selectedStyle = 0;
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){

                                JSONArray styleArr = response.getJSONArray("style_data");
                                for(int i=0; i<styleArr.length(); i++){
                                    JSONObject styleObj = styleArr.getJSONObject(i);
                                    ImageStyle imageStyle = new ImageStyle();
                                    imageStyle.setIdx(styleObj.getInt("id"));
                                    imageStyle.setTitle(styleObj.getString("name_eng"));
                                    imageStyle.setFrame(styleObj.getString("url_front"));
                                    imageStyle.setRightSide(styleObj.getString("url_right"));
                                    imageStyle.setBottomSide(styleObj.getString("url_bottom"));
                                    imageStyle.setPrice(Float.parseFloat(styleObj.getString("price")));

                                    imageStyle.setFrameWidth(Integer.parseInt(styleObj.getString("frame_size_width")));
                                    imageStyle.setFrameHeight(Integer.parseInt(styleObj.getString("frame_size_height")));
                                    imageStyle.setPhotoWidth(Integer.parseInt(styleObj.getString("photo_size_width")));
                                    imageStyle.setPhotoHeight(Integer.parseInt(styleObj.getString("photo_size_height")));

                                    Commons.imageStyles.add(imageStyle);

                                    LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = layoutInflater.inflate(R.layout.item_frame_list, null);
                                    ImageView picture = (ImageView) view.findViewById(R.id.frame_picture);
                                    ImageView right = (ImageView) view.findViewById(R.id.right);
                                    ImageView bottom = (ImageView) view.findViewById(R.id.bottom);
                                    TextView title = (TextView)view.findViewById(R.id.frame_title);
                                    View indicator = (View) view.findViewById(R.id.indicator);
                                    Picasso.with(getApplicationContext()).load(imageStyle.getFrame()).into(picture);
                                    Picasso.with(getApplicationContext()).load(imageStyle.getRightSide()).into(right);
                                    Picasso.with(getApplicationContext()).load(imageStyle.getBottomSide()).into(bottom);
                                    title.setText(imageStyle.getTitle());
                                    if(i == 0){
                                        title.setTextColor(getColor(R.color.colorPrimary));
                                        indicator.setVisibility(View.VISIBLE);
                                    }else{
                                        title.setTextColor(getColor(R.color.text));
                                        indicator.setVisibility(View.INVISIBLE);
                                    }
                                    int finalI = i;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            resetFrameList();
                                            title.setTextColor(getColor(R.color.colorPrimary));
                                            indicator.setVisibility(View.VISIBLE);
                                            selectedStyle = finalI;    // i + 1 ==> imageStyle ID
                                            loadSelectedImages(imageStyle);
//                    loadImages();
                                        }
                                    });
                                    frameViews.add(view);
                                    ((LinearLayout)findViewById(R.id.frames)).addView(view);

                                    if(i == styleArr.length() - 1){
                                        loadSelectedImages(Commons.imageStyles.get(0));
                                    }
                                }

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

    private void getShippingAddressList(){

        Commons.addressList.clear();

//        SharedPreferences shref;
//        SharedPreferences.Editor editor;
//        shref = getSharedPreferences("PREF_ADDRESSLIST", Context.MODE_PRIVATE);
//
//        Gson gson = new Gson();
//        String response = shref.getString("addrList" , "");
//        ArrayList<Address> addressArrayList = gson.fromJson(response,
//                new TypeToken<List<Address>>(){}.getType());
//
//        if(addressArrayList != null){
//            Commons.addressList = addressArrayList;
//        }

        AndroidNetworking.post(ReqConst.SERVER_URL + "getShippingAddress")
                .addBodyParameter("userId", String.valueOf(Commons.thisUser.getIdx()))
                .addBodyParameter("authorization", "pixil.mn")
                .setTag("get_shipping_address")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE!!!", response.toString());
                        // do anything with response
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){

                                JSONArray jsonArray = response.getJSONArray("shippingAddresses");
                                for(int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Address address = new Address();
                                    address.setAddress(object.getString("address"));
                                    address.setDistrict(object.getString("district"));
                                    address.setCity(object.getString("city"));
                                    address.setZipcode(object.getString("zip_code"));
                                    address.setProvince(object.getString("province"));

                                    Commons.addressList.add(0, address);
                                }


                            }else if(result.equals("1")){
                                showToast(getString(R.string.no_address));
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
                    }
                });

    }

}
































