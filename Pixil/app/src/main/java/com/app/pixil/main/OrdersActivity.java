package com.app.pixil.main;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.pixil.R;
import com.app.pixil.adapters.OrderListAdapter;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.ReqConst;
import com.app.pixil.models.ImageStyle;
import com.app.pixil.models.Order;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersActivity extends BaseActivity {

    ListView listView;
    ArrayList<Order> orders = new ArrayList<>();
    OrderListAdapter adapter = new OrderListAdapter(this);

    AVLoadingIndicatorView progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        progressBar = (AVLoadingIndicatorView)findViewById(R.id.loading_bar);
        listView = (ListView)findViewById(R.id.list);
        getOrders();

    }

    private void getOrders(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getMyOrders")
                .addBodyParameter("authorization", "pixil.mn")
                .addBodyParameter("userId", String.valueOf(Commons.thisUser.getIdx()))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("RESPONSE!!!", response.toString());
                        // do anything with response
                        progressBar.setVisibility(View.GONE);
                        try {
                            String result = response.getString("result_code");
                            if(result.equals("0")){

                                JSONArray orderArr = response.getJSONArray("orders");
                                for(int i=orderArr.length() - 1; i>=0; i--){
                                    JSONObject object = orderArr.getJSONObject(i);
                                    JSONObject orderObj = object.getJSONObject("order");
                                    Order order = new Order();
                                    order.setId(orderObj.getInt("id"));
                                    order.setFullName(orderObj.getString("fullName"));
                                    order.setOrderDate(orderObj.getString("order_date"));
                                    order.setStatus(orderObj.getString("status"));
                                    order.setPixilPrice(Float.parseFloat(orderObj.getString("pixil_price")));
                                    order.setDeliveryDate(orderObj.getString("delivery_date"));
                                    order.setEmail(orderObj.getString("emailAddress"));
                                    order.setPixils(Integer.parseInt(orderObj.getString("pixils")));
                                    order.setPhoneNumber(orderObj.getString("phoneNum"));
                                    order.setAddress(orderObj.getString("address"));
                                    order.setProvince(orderObj.getString("province"));
                                    order.setCity(orderObj.getString("city"));
                                    order.setDistrict(orderObj.getString("district"));

                                    order.setZipCode(orderObj.getString("zip_code"));
                                    order.setAdditionalPrice(Float.parseFloat(orderObj.getString("additional_price")));
//                                    order.setPrintDate(orderObj.getString("print_date"));
//                                    order.setTrackingNumber(orderObj.getString("tracking_number"));

                                    ArrayList<String> pictureList = new ArrayList<>();
                                    JSONArray pictures = object.getJSONArray("pixils");
                                    for(int j=0;j<pictures.length(); j++){
                                        String pic = (String) pictures.get(j);
                                        Log.d("PICTURE!!!", pic);
                                        pictureList.add(pic);
                                    }

                                    order.setPictures(pictureList);

                                    orders.add(0, order);
                                }

                                adapter.setDatas(orders);

                                if(adapter.getCount() == 0){
                                    ((TextView)findViewById(R.id.noresult)).setVisibility(View.VISIBLE);
                                }else {
                                    ((TextView)findViewById(R.id.noresult)).setVisibility(View.GONE);
                                }

                                listView.setAdapter(adapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    public void back(View view){
        onBackPressed();
    }
}



























