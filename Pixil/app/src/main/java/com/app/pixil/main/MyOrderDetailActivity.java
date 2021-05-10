package com.app.pixil.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyOrderDetailActivity extends BaseActivity {

    ImageView chatButton;
    LinearLayout pictures;
    TextView title, ordered, shipped, delivery;
    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);

        pictures = (LinearLayout)findViewById(R.id.pictures);

        title = (TextView)findViewById(R.id.title);
        ordered = (TextView)findViewById(R.id.ordered);
        shipped = (TextView)findViewById(R.id.shipped);
        delivery = (TextView)findViewById(R.id.delivery);

        chatButton = (ImageView)findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
                startActivity(intent);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        title.setText(getString(R.string.order_number2_) + " " + String.valueOf(Commons.order.getId()));
        ordered.setText(getString(R.string.ordered_) + " " + formatter.format(new Date(Long.parseLong(Commons.order.getOrderDate()) * 1000)));
        shipped.setText(getString(R.string.shipped_) + " " + Commons.order.getStatus());
        delivery.setText(getString(R.string.deliveryby_) + " " + formatter.format(new Date(Long.parseLong(Commons.order.getDeliveryDate()) * 1000)));

        getOrderedPictures();

    }

    private void getOrderedPictures(){

        pictures.removeAllViews();
        for(String image:Commons.order.getPictures()){
            LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_ordered_pictures, null);
            ImageView picture = (ImageView)view.findViewById(R.id.picture);
            Picasso.with(getApplicationContext())
                    .load(image)
                    .into(picture, new Callback() {
                        @Override
                        public void onSuccess() {
                            ((AVLoadingIndicatorView)view.findViewById(R.id.loading_bar)).setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            ((AVLoadingIndicatorView)view.findViewById(R.id.loading_bar)).setVisibility(View.GONE);
                        }
                    });

            int index = Commons.orderedImages.indexOf(image);
            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                    intent.putExtra("image", image);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) MyOrderDetailActivity.this, picture, getString(R.string.transition));
                    startActivity(intent, options.toBundle());
                }
            });
            pictures.addView(view);
        }

    }

    public void back(View view){
        onBackPressed();
    }

}








































