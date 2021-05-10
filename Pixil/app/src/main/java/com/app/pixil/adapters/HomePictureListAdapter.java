package com.app.pixil.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.pixil.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomePictureListAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Integer> _datas = new ArrayList<>();
    private ArrayList<Integer> _alldatas = new ArrayList<>();

    public HomePictureListAdapter(Context context) {
        super();
        this.context = context;
    }

    public void setDatas(ArrayList<Integer> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_picture_list, collection, false);
        ImageView pagerImage = (ImageView) layout.findViewById(R.id.picture);

        Picasso.with(context)
                .load(_datas.get(position))
                .into(pagerImage);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        collection.addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return this._datas.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
