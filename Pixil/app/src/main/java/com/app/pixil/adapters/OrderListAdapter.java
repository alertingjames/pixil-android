package com.app.pixil.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.pixil.R;
import com.app.pixil.commons.Commons;
import com.app.pixil.main.MyOrderDetailActivity;
import com.app.pixil.models.Order;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<Order> _datas = new ArrayList<>();
    private ArrayList<Order> _alldatas = new ArrayList<>();

    public OrderListAdapter(Context context){

        super();
        this._context = context;
    }

    public void setDatas(ArrayList<Order> datas) {

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
        return 2;
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.item_orders, parent, false);

            holder.picture = (ImageView) convertView.findViewById(R.id.picture);
            holder.orderID = (TextView) convertView.findViewById(R.id.orderID);
            holder.orderDate = (TextView) convertView.findViewById(R.id.orderDate);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.pixils = (TextView) convertView.findViewById(R.id.pixils);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final Order entity = (Order) _datas.get(position);

        holder.orderID.setText(_context.getString(R.string.order_number2_) + " " + String.valueOf(entity.getId()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        holder.orderDate.setText(formatter.format(new Date(Long.parseLong(entity.getOrderDate()) * 1000)));
        holder.pixils.setText(_context.getString(R.string.pixils_) + " " + String.valueOf(entity.getPixils()));
        holder.status.setText(entity.getStatus());

        Glide.with(_context)
                .load(entity.getPictures().get(0))
                .error(R.drawable.noresult)
                .placeholder(R.drawable.noresult)
                .into(holder.picture);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.order = entity;
                Intent intent = new Intent(_context, MyOrderDetailActivity.class);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {
            for (Order order : _alldatas){

                if (order instanceof Order) {

                    String value = ((Order) order).getOrderDate().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(order);
                    }
                    else {
                        value = ((Order) order).getDeliveryDate().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(order);
                        }else {
                            value = ((Order) order).getStatus().toLowerCase();
                            if (value.contains(charText)) {
                                _datas.add(order);
                            }else {
                                value = ((Order) order).getAddress().toLowerCase();
                                if (value.contains(charText)) {
                                    _datas.add(order);
                                }else {

                                }
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        ImageView picture;
        TextView orderID;
        TextView orderDate;
        TextView pixils;
        TextView status;
    }
}





