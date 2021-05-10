package com.app.pixil.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pixil.R;
import com.app.pixil.commons.Commons;
import com.app.pixil.main.MainActivity;
import com.app.pixil.main.PolicyActivity;
import com.app.pixil.main.SelectPhotosActivity;
import com.app.pixil.main.TermsActivity;
import com.app.pixil.models.GalleryImage;
import com.app.pixil.models.Image;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;

public class PictureListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Image> _datas = new ArrayList<>();
    private ArrayList<Image> _alldatas = new ArrayList<>();

    public PictureListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Image> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_gallery_picture, parent, false);

            holder.frameLayout = (FrameLayout)convertView.findViewById(R.id.frame);
            holder.picture = (ImageView) convertView.findViewById(R.id.picture);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Image image = (Image) _datas.get(position);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layoutParams.gravity = Gravity.CENTER;
        holder.picture.getLayoutParams().height = holder.picture.getLayoutParams().width;
        holder.picture.setLayoutParams(layoutParams);

        if(image.getPath().length() > 0){
            Glide.with(_context)
                    .load("file://" + image.getPath())
                    .placeholder(R.drawable.logo_110)
                    .into(holder.picture);
        }

        Log.d("IMAGE PATH: ", String.valueOf(image.getPath()));

        if (image.isSelected()){
            holder.picture.setColorFilter(ContextCompat.getColor(_context, R.color.tint), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.frameLayout.setBackgroundResource(R.drawable.blue_stroke);
        }
        else {
            holder.picture.setColorFilter(null);
            holder.frameLayout.setBackgroundResource(0);
        }

        if (Commons.selectedImagePaths.contains(image.getPath())){
            holder.picture.setColorFilter(ContextCompat.getColor(_context, R.color.tint), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.frameLayout.setBackgroundResource(R.drawable.blue_stroke);
        }
        else {
            holder.picture.setColorFilter(null);
            holder.frameLayout.setBackgroundResource(0);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!image.isSelected()){

                    Bitmap bitmap = getBitmap(image.getPath());

                    // Check image pixels (quality)
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(image.getPath(), options);
                    int width = options.outWidth;
                    int height = options.outHeight;

                    Log.d("PIXELS!!!", String.valueOf(width) + "/" +String.valueOf(height));


                    if(width * height < 800 * 800){
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(_context);
                        LayoutInflater layoutInflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = layoutInflater.inflate(R.layout.low_image_alert, null);

                        builder.setView(view);
                        final android.support.v7.app.AlertDialog dialog = builder.create();

                        dialog.show();

                        TextView noSelButton = (TextView)view.findViewById(R.id.btn_nosel);
                        TextView selButton = (TextView)view.findViewById(R.id.btn_sel);

                        noSelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        selButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.picture.setColorFilter(ContextCompat.getColor(_context, R.color.tint), android.graphics.PorterDuff.Mode.MULTIPLY);
                                holder.frameLayout.setBackgroundResource(R.drawable.blue_stroke);
                                image.setSelected(true);
                                Commons.selectedImages.add(image);
                                if(Commons.selectPhotosActivity != null){
                                    if(Commons.selectedImages.size() < 3){
                                        Commons.selectPhotosActivity.textView.setText(_context.getString(R.string.selectphotoscap));
//                                        Commons.selectPhotosActivity.textView0.setText(_context.getString(R.string.selectphotoscap2));
                                    }else{
                                        Commons.selectPhotosActivity.textView.setText(String.valueOf(Commons.selectedImages.size()) + " " + (Commons.selectedImages.size() == 1?_context.getString(R.string.pixil):_context.getString(R.string.pixils)) +
                                                " " + _context.getString(R.string.are) + " ₮" + Commons.selectPhotosActivity.getFormatedAmount(Commons.selectedImages.size() * 15000 + 5000));
                                    }
                                    Commons.selectPhotosActivity.textView0.setText(
                                            (Commons.selectedImages.size() < 3)? _context.getString(R.string.select) + " " + String.valueOf(3 - Commons.selectedImages.size()) + " " + _context.getString(R.string.more) : "");
                                }
                                dialog.dismiss();
                            }
                        });
                    }else{
                        holder.picture.setColorFilter(ContextCompat.getColor(_context, R.color.tint), android.graphics.PorterDuff.Mode.MULTIPLY);
                        holder.frameLayout.setBackgroundResource(R.drawable.blue_stroke);
                        image.setSelected(true);
                        Commons.selectedImages.add(image);
                        if(Commons.selectPhotosActivity != null){
                            if(Commons.selectedImages.size() < 3){
                                Commons.selectPhotosActivity.textView.setText(_context.getString(R.string.selectphotoscap));
//                                Commons.selectPhotosActivity.textView0.setText(_context.getString(R.string.selectphotoscap2));
                            }else{
                                Commons.selectPhotosActivity.textView.setText(String.valueOf(Commons.selectedImages.size()) + " " + (Commons.selectedImages.size() == 1?_context.getString(R.string.pixil):_context.getString(R.string.pixils)) +
                                        " " + _context.getString(R.string.are) + " ₮" + Commons.selectPhotosActivity.getFormatedAmount(Commons.selectedImages.size()*15000 + 5000));

                            }
                            Commons.selectPhotosActivity.textView0.setText(
                                    (Commons.selectedImages.size() < 3)? _context.getString(R.string.select) + " " + String.valueOf(3 - Commons.selectedImages.size()) + " " + _context.getString(R.string.more) : "");
                        }
                    }
                }else {
                    int index = Commons.selectedImages.indexOf(image);
                    if (index >= 0){
                        holder.picture.setColorFilter(null);
                        holder.frameLayout.setBackground(null);
                        image.setSelected(false);
                        Commons.selectedImages.remove(index);
                        if(Commons.selectPhotosActivity != null){
                            if(Commons.selectedImages.size() < 3){
                                Commons.selectPhotosActivity.textView.setText(_context.getString(R.string.selectphotoscap));
//                                Commons.selectPhotosActivity.textView0.setText(_context.getString(R.string.selectphotoscap2));
                            }else{
                                Commons.selectPhotosActivity.textView.setText(String.valueOf(Commons.selectedImages.size()) + " " + (Commons.selectedImages.size() == 1?_context.getString(R.string.pixil):_context.getString(R.string.pixils)) +
                                        " " + _context.getString(R.string.are) + " ₮" + Commons.selectPhotosActivity.getFormatedAmount(Commons.selectedImages.size()*15000 + 5000));

                            }
                            Commons.selectPhotosActivity.textView0.setText(
                                    (Commons.selectedImages.size() < 3)? _context.getString(R.string.select) + " " + String.valueOf(3 - Commons.selectedImages.size()) + " " + _context.getString(R.string.more) : "");
                        }
                    }
                }
            }
        });

        return convertView;
    }

    protected Bitmap getBitmap(String photoPath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        return bitmap;
    }

    protected int[] getImageData(Bitmap img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] data = new int[w * h];
        img.getPixels(data, 0, w, 0, 0, w, h);
        return data;
    }

    class CustomHolder {
        ImageView picture;
        FrameLayout frameLayout;
    }

}


























