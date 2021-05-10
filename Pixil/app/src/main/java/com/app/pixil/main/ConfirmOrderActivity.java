package com.app.pixil.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.Constants;
import com.app.pixil.commons.ReqConst;
import com.app.pixil.models.Image;
import com.app.pixil.models.ImageStyle;
import com.app.pixil.utils.ColorUtil;
import com.iamhabib.easy_preference.EasyPreference;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmOrderActivity extends BaseActivity {

    Button orderButton;
    TextView districtBox, cityBox, zipcodeBox, provinceBox;
    TextView pixilsBox, namebox, phoneBox, addressBox, datetimeBox, pixilsetBox, pixilsetpriceBox, additionalsetBox, additionalsetpriceBox, totalBox, totalpriceBox;
    AVLoadingIndicatorView progressBar;
    ArrayList<File> fileList = new ArrayList<>();
    ImageStyle selectedImageStyle;
    public static DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        pixilsBox = (TextView)findViewById(R.id.pixils);
        namebox = (TextView)findViewById(R.id.name);
        phoneBox = (TextView)findViewById(R.id.phone_number);
        addressBox = (TextView)findViewById(R.id.address);
        datetimeBox = (TextView)findViewById(R.id.datetime);
        pixilsetBox = (TextView)findViewById(R.id.pixilset);
        pixilsetpriceBox = (TextView)findViewById(R.id.pixilsetprice);
        additionalsetBox = (TextView)findViewById(R.id.additionalset);
        additionalsetpriceBox = (TextView)findViewById(R.id.additionalsetprice);
        totalBox = (TextView)findViewById(R.id.total);
        totalpriceBox = (TextView)findViewById(R.id.totalprice);

        districtBox = (TextView)findViewById(R.id.district);
        cityBox = (TextView)findViewById(R.id.city);
        zipcodeBox = (TextView)findViewById(R.id.zipcode);
        provinceBox = (TextView)findViewById(R.id.province);

        namebox.setText(Commons.thisUser.getName());
        phoneBox.setText(Commons.thisUser.getPhoneNumber());

        if(Commons.address != null){
            addressBox.setText(Commons.address.getAddress());
            districtBox.setText(Commons.address.getDistrict());
            cityBox.setText(Commons.address.getCity());
            zipcodeBox.setText(Commons.address.getZipcode());
            provinceBox.setText(Commons.address.getProvince());
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(new Date().getTime() + Constants.ESTIMATE_DELIVERY_FROM_NOW * 86400 * 1000));
            datetimeBox.setText(date);
        }

        selectedImageStyle = Commons.imageStyles.get(Commons.stylePhotosActivity.selectedStyle);
        Commons.orderedImages.clear();
        for(int i=0; i< Commons.selectedImages.size(); i++){
            Image image = new Image();
//            image.setFile(createBitmapFromLayout(selectedImageStyle, Commons.selectedImages.get(i)));
            image.setFile(Commons.selectedImages.get(i).getFile());
            for(int j=0; j<Commons.selectedImages.get(i).getCopyCount(); j++)
                Commons.orderedImages.add(image);
            if(i == Commons.selectedImages.size() - 1){
                pixilsBox.setText(String.valueOf(Commons.orderedImages.size()));
                pixilsetBox.setText("3");
                pixilsetpriceBox.setText("₮50,000");
                additionalsetBox.setText(String.valueOf(Commons.orderedImages.size() - 3));
                additionalsetpriceBox.setText("₮" + getFormatedAmount((int) ((int) (Commons.orderedImages.size() - 3) * selectedImageStyle.getPrice())).replace(".0",""));
                totalBox.setText(String.valueOf(Commons.orderedImages.size()));
                int discount = 0;
                String discountStr = EasyPreference.with(getApplicationContext()).getString("discount", "");
                Log.d("DISCOUNT!!!", discountStr);
                if(discountStr.length() > 0)discount = Integer.parseInt(discountStr);
                totalpriceBox.setText("₮" + getFormatedAmount((int) ((int) ((Commons.orderedImages.size() - 3) * selectedImageStyle.getPrice() + 50000) * (1 - ((float)discount/100)))).replace(".0",""));
                for(Image img: Commons.orderedImages){

                    Log.d("FILE SIZE 222 !!!", calculateFileSize(img.getFile().getPath()));

                    Bitmap originalImage = BitmapFactory.decodeFile(img.getFile().getPath());

                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();

                    File file = img.getFile();
                    if(width != height){
                        file = cropResized(img.getFile().getPath(), width, height);
                    }

                    assert file != null;
                    Log.d("FILE SIZE 333 !!!", calculateFileSize(file.getPath()));

                    fileList.add(file);
                }
            }
        }

        orderButton = (Button)findViewById(R.id.orderButton);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processOrder();
            }
        });

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

    private File cropResized(String imageFilePath, int width, int height){
        Bitmap croppedBitmap = null;
        int x = 0, y = 0;
        if(width > height) {
            x = (width - height)/2;
            width = height;
        }
        else if(width < height) {
            y = (height - width)/2;
            height = width;
        }
        Rect rect = new Rect(x, y, x + width, y + height);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1)
        {
            BitmapRegionDecoder decoder= null;
            try {
                decoder = BitmapRegionDecoder.newInstance(imageFilePath, true);
                croppedBitmap = decoder.decodeRegion(rect, null);
                decoder.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Bitmap bitmapOriginal = BitmapFactory.decodeFile(imageFilePath, null);
            croppedBitmap = Bitmap.createBitmap(bitmapOriginal,rect.left,rect.top,rect.width(),rect.height());
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        assert croppedBitmap != null;
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);

        Log.d("DIMENSIONS !!!", String.valueOf(croppedBitmap.getWidth()) + " / " + String.valueOf(croppedBitmap.getHeight()));

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";
        File storageDir = new File(path);
        if (!storageDir.exists() && !storageDir.mkdir()) {
            return null;
        }

        FileOutputStream outStream = null;
        File file = new File(path,
                System.currentTimeMillis() + ".jpg");
        try {
            file.createNewFile();
            outStream = new FileOutputStream(file);
            outStream.write(byteArrayOutputStream.toByteArray());
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void processOrder(){
//        progressBar.setVisibility(View.VISIBLE);
        ((LinearLayout)findViewById(R.id.progressLayout)).setVisibility(View.VISIBLE);
        orderButton.setVisibility(View.GONE);
        showToast(getString(R.string.waitwhileordering));

        AndroidNetworking.upload(ReqConst.SERVER_URL + "saveOrder")
                .addMultipartFileList("photo[]", fileList)
                .addMultipartParameter("userId", String.valueOf(Commons.thisUser.getIdx()))
                .addMultipartParameter("address", Commons.address.getAddress())
                .addMultipartParameter("district", Commons.address.getDistrict())
                .addMultipartParameter("city", Commons.address.getCity())
                .addMultipartParameter("zipCode", Commons.address.getZipcode())
                .addMultipartParameter("province", Commons.address.getProvince())
                .addMultipartParameter("productId", String.valueOf(Commons.stylePhotosActivity.selectedStyle + 1))   // i + 1 ==> imageStyle ID  (product ID)
                .addMultipartParameter("authorization", "pixil.mn")
                .setPriority(Priority.HIGH)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        String uploaded = String.valueOf((int)bytesUploaded*100/totalBytes);
                        Log.d("UPLOADED!!!", uploaded);
                        ((TextView)findViewById(R.id.progressText)).setText(uploaded);
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        Log.d("RESPONSE!!!", response.toString());
                        // do anything with response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                progressBar.setVisibility(View.GONE);
                                ((LinearLayout)findViewById(R.id.progressLayout)).setVisibility(View.GONE);
                            }
                        });
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){
                                showToast(getString(R.string.orderplaced));
                                String price = df.format(Double.parseDouble(totalpriceBox.getText().toString().trim().replace("₮","").replace(",","").trim()));
                                Intent intent = new Intent(getApplicationContext(), OrderPlacedActivity.class);
                                intent.putExtra("order_id", response.getString("order_id"));
                                intent.putExtra("price", price);
                                startActivity(intent);
                                finish();
                            }else{
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
//                        Log.d("ERROR!!!", error.getResponse().toString());
//                        showToast(getString(R.string.serverissue));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                progressBar.setVisibility(View.GONE);
                                ((LinearLayout)findViewById(R.id.progressLayout)).setVisibility(View.GONE);
                            }
                        });

                        showToast(getString(R.string.orderplaced));
                        String price = df.format(Double.parseDouble(totalpriceBox.getText().toString().trim().replace("₮","").replace(",","").trim()));
                        Intent intent = new Intent(getApplicationContext(), OrderPlacedActivity.class);
                        intent.putExtra("order_id", "");
                        intent.putExtra("price", price);
                        startActivity(intent);
                        finish();
                    }
                });


    }








    String APP_PATH_SD_CARD = "/pixil/";
    String APP_THUMBNAIL_PATH_SD_CARD = "orders";

    private File saveImageToExternalStorage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, System.currentTimeMillis() + ".png");
            file.createNewFile();
            fOut = new FileOutputStream(file);

// 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            return file;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return null;
        }
    }

    public File createBitmapFromLayout(ImageStyle imageStyle, Image image) {
        File resultFile = null;

        View view = image.getView();

        //Provide it with a layout params. It should necessarily be wrapping the
        //content as we not really going to have a parent for it.
        view.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        //Pre-measure the view so that height and width don't remain null.
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //Assign a size and position to the view and all of its descendants
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        //Create the bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        //Create a canvas with the specified bitmap to draw into
        Canvas c = new Canvas(bitmap);

        //Render this view (and all of its children) to the given Canvas
        view.draw(c);
        resultFile = saveImageToExternalStorage(bitmap);
        Log.d("RESULTFILE!!!", resultFile.getPath());

        if(resultFile != null){
            return resultFile;
        }else {
            //Get the image to be changed from the drawable, drawable-xhdpi, drawable-hdpi,etc folder.
            Drawable sourceDrawable = getResources().getDrawable(R.drawable.insta_1);
            //Convert drawable in to bitmap
            Bitmap bitmap1 = ColorUtil.convertDrawableToBitmap(sourceDrawable);

            resultFile = saveImageToExternalStorage(bitmap1);

            if(resultFile != null){
                return resultFile;
            }
        }
        return resultFile;
    }



    public void back(View view){
        onBackPressed();
    }

}





































