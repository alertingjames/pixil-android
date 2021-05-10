package com.app.pixil.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.app.pixil.R;
import com.app.pixil.classes.ItemCLickListener;
import com.app.pixil.commons.Commons;
import com.app.pixil.models.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FramePictureListAdapter extends RecyclerView.Adapter<FramePictureListAdapter.ViewHolder> {

    ArrayList<Image> datas = new ArrayList<>();
    Context context;

    public FramePictureListAdapter(Context context, ArrayList<Image> datas) {
        super();
        this.context = context;
        this.datas.addAll(datas);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_frame_image, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        Image image = datas.get(i);

//      frame.setImageResource(Commons.imageStyles.get(selectedStyle).getFrameRes());
        Picasso.with(context).load(Commons.imageStyles.get(Commons.stylePhotosActivity.selectedStyle).getFrameRes()).into(viewHolder.frame);
        Log.d("FRAME SIZE!!!", String.valueOf(viewHolder.frame.getWidth()) + "/" + String.valueOf(viewHolder.frame.getHeight()));
        int margin = 0;
        if(Commons.stylePhotosActivity.selectedStyle == 0){
            margin = 0;
        }else if(Commons.stylePhotosActivity.selectedStyle == 1){
            margin = 18;
        }else if(Commons.stylePhotosActivity.selectedStyle == 2){
            margin = 70;
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(margin,margin, margin, margin);
        viewHolder.layout.setLayoutParams(layoutParams);

        Picasso.with(context)
                .load(image.getFile())
                .into(viewHolder.picture);
        image.setView(viewHolder.itemView);
        Log.d("ImageView Size!!!", String.valueOf(viewHolder.picture.getWidth()) + "/" + String.valueOf(viewHolder.picture.getHeight()));
        int index = Commons.selectedImages.indexOf(image);

        viewHolder.setClickListener(new ItemCLickListener() {
            @Override
            public void onClick(View view, int position, boolean isClick) {
                if(context == Commons.stylePhotosActivity)
                    Commons.stylePhotosActivity.showAlertDialogForEdit(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView frame;
        public ImageView picture;
        public LinearLayout layout;
        private ItemCLickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            frame = (ImageView) itemView.findViewById(R.id.frame_picture);
            picture = (ImageView)itemView.findViewById(R.id.picture);
            FrameLayout pictureLayout = (FrameLayout)itemView.findViewById(R.id.picture_layout);
            layout = (LinearLayout)itemView.findViewById(R.id.layout);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(ItemCLickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

}

