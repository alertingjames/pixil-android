package com.app.pixil.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.ReqConst;
import com.app.pixil.models.Order;
import com.app.pixil.models.User;
import com.bumptech.glide.Glide;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactUsActivity extends BaseActivity {

    LinearLayout contactsFrame;
    ImageView cancelButton;

    View space;

    LinearLayout layout;
    LinearLayout layout_2;
    ImageView sendButton, attachButton, ok, cancel;
    EditText messageBox;
    ScrollView scrollView;
    TextView typeStatus;
    Firebase reference1, reference2, reference3, reference4, reference5, reference7;
    int is_talking=0;
    int is_talkingR=0;
    boolean is_typing=false;
    AVLoadingIndicatorView progressBar;
    boolean startTalking=false;

    ImageView image;
    LinearLayout imagePortion;
    String imageFile="";
    String messageReceivedTime = "";
    Uri downloadUri = null;
    String videoThumbStr = "";
    String imageStr = "";
    boolean userOnlineF = false;
    CircleImageView userPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        changeStatusBarColor("#4073D5");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        User user = new User();
        user.setIdx(1);
        user.setPhoto("http://pixil.mn/public/avatar/admins/1570064628.jpg");
        user.setName(getString(R.string.admin));

        Commons.user = user;
        Commons.thisUser.setName(Commons.thisUser.getFirstName() + " " + Commons.thisUser.getLastName());

        Firebase.setAndroidContext(this);
        reference1 = new Firebase(ReqConst.FIREBASE_URL + "message/user" + Commons.thisUser.getIdx() + "_" + Commons.user.getIdx());
        reference2 = new Firebase(ReqConst.FIREBASE_URL + "message/user" + Commons.user.getIdx() + "_" + Commons.thisUser.getIdx());
        reference3 = new Firebase(ReqConst.FIREBASE_URL + "notification/user" + Commons.user.getIdx() + "/" + Commons.thisUser.getIdx());
        reference4 = new Firebase(ReqConst.FIREBASE_URL + "status/user" + Commons.user.getIdx() + "_" + Commons.thisUser.getIdx());
        reference5 = new Firebase(ReqConst.FIREBASE_URL + "status/user" + Commons.thisUser.getIdx() + "_" + Commons.user.getIdx());
        reference7 = new Firebase(ReqConst.FIREBASE_URL + "notification/user" + Commons.thisUser.getIdx() + "/" + Commons.user.getIdx());

        contactsFrame = (LinearLayout)findViewById(R.id.contactsFrame);
        cancelButton = (ImageView)findViewById(R.id.cancelButton);

        space = (View)findViewById(R.id.space);

        userPhoto = (CircleImageView)findViewById(R.id.userPhoto);

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status

//                        if(contactsFrame.getVisibility() == View.VISIBLE){
//                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_out);
//                            contactsFrame.setAnimation(animation);
//                            contactsFrame.setVisibility(View.GONE);
//                            cancelButton.setVisibility(View.GONE);
//                        }

                    }
                });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactsFrame.getVisibility() == View.VISIBLE){
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                    contactsFrame.setAnimation(animation);
                    contactsFrame.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);

                    space.setVisibility(View.VISIBLE);
                }
            }
        });

        setupUI(findViewById(R.id.chatFrame), this);

        messageBox = (EditText)findViewById(R.id.messageBox);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (LinearLayout) findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        imagePortion=(LinearLayout)findViewById(R.id.imagePortion);
        image=(ImageView) findViewById(R.id.image);
        cancel=(ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePortion.setVisibility(View.GONE);
            }
        });
        ok=(ImageView) findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                imagePortion.setVisibility(View.GONE);
            }
        });
        progressBar = (AVLoadingIndicatorView) findViewById(R.id.loading_bar);
        attachButton=(ImageView)findViewById(R.id.attachButton);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ContactUsActivity.this);
            }
        });

        typeStatus = (TextView)findViewById(R.id.typeStatus);

        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String messageText = messageBox.getText().toString().trim();
//                if(!is_typing){
//                    is_typing=true;
//                    if(messageText.length() > 0){
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("online", "typing");
//                        map.put("time", String.valueOf(new Date().getTime()));
//                        map.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
//                        reference4.removeValue();
//                        reference4.push().setValue(map);
//                    }
//                }else {
//                    if(messageText.length()==0){
//                        is_typing=false;
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("online", "online");
//                        map.put("time", String.valueOf(new Date().getTime()));
//                        map.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
//                        reference4.removeValue();
//                        reference4.push().setValue(map);
//                    }
//                }
            }
        });

//        online("true");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageBox.getText().toString().trim();
                if(messageText.length() > 0){
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("message", messageText);
//                    map.put("time", String.valueOf(new Date().getTime()));
//                    map.put("image", "");
//                    map.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
//                    map.put("name", Commons.thisUser.getName());
//
//                    online("true");
//                    reference1.push().setValue(map);
//                    reference2.push().setValue(map);
//                    messageBox.setText("");
//                    is_typing=false;
//
//                    Map<String, String> map2 = new HashMap<String, String>();
//                    map2.put("message", messageText);
//                    map2.put("time", String.valueOf(new Date().getTime()));
//                    map2.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
//                    map2.put("sender_phone", Commons.thisUser.getPhoneNumber());
//                    map2.put("sender_name", Commons.thisUser.getName());
//                    reference3.removeValue();
//                    reference3.push().setValue(map2);
//
//                    sendFcmNotification(messageText);

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("time", String.valueOf(new Date().getTime()));
                    map.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
                    map.put("sender_name", Commons.thisUser.getName());
                    map.put("sender_img", Commons.thisUser.getPhoto());
                    map.put("receiver_id", String.valueOf(Commons.user.getIdx()));
                    map.put("receiver_name", Commons.user.getName());
                    map.put("receiver_img", Commons.user.getPhoto());

                    reference1.push().setValue(map);
                    messageBox.setText("");

                }
            }
        });

//        reference5.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map map = dataSnapshot.getValue(Map.class);
//                String online = map.get("online").toString();
//                String time = map.get("time").toString();
//
//                if(online.equals("online")){
//                    typeStatus.setVisibility(View.GONE);
//                    userOnlineF = true;
//                }else if(online.equals("offline")){
//                    userOnlineF = false;
//                }else {
//                    typeStatus.setVisibility(View.VISIBLE);
//                    try{
//                        if(Commons.user.getName()!=null){
//                            typeStatus.setText(Commons.user.getName()+" " + getString(R.string.istyping));
//                        }
//                    }catch (NullPointerException e){
//                        e.printStackTrace();
//                    }catch (Exception e){}
//
//                    userOnlineF = true;
//
//                    scrollView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                        }
//                    });
//
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String time = map.get("time").toString();
                String image = map.get("sender_img").toString();
                String senderId = map.get("sender_id").toString();
                String name = map.get("sender_name").toString();
                String key = dataSnapshot.getKey();

                if(senderId.equals(String.valueOf(Commons.thisUser.getIdx()))){
                    addMessageBox(message, time, "", key, 1);
                }
                else{
                    addMessageBox(message, time, "", key, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

//        reference7.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                try{
//                    Map map = dataSnapshot.getValue(Map.class);
//                    String dateTime = map.get("time").toString();
//                    if(Math.abs(Long.parseLong(dateTime) - Long.parseLong(messageReceivedTime)) < 5000) reference7.child(dataSnapshot.getKey()).removeValue();
//                }catch (NullPointerException e){
//                    e.printStackTrace();
//                }catch (FirebaseException e){
//                    e.printStackTrace();
//                }catch (NumberFormatException e){
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        getAdminData();

    }

    private void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setupUI(View view, Activity activity) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    try{
                        hideSoftKeyboard(activity);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void back(View view){
//        online("false");
        onBackPressed();

    }

    public void online(String status){
        Map<String, String> map = new HashMap<String, String>();
        if(status.equals("true"))
            map.put("online", "online");
        else map.put("online", "offline");
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
        reference4.removeValue();
        reference4.push().setValue(map);
        if(status.equals("false")){
            reference7 = null;
            finish();
        }
    }

    public void addMessageBox(final String message, String time, final String imageStr, String key, final int type){

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.chat_history_area, null);

        FrameLayout photoFrame = (FrameLayout)dialogView.findViewById(R.id.photoFrame);
        final CircleImageView photo = (CircleImageView) dialogView.findViewById(R.id.photo);
        Picasso.with(getApplicationContext())
                .load(R.drawable.logo_110)
                .error(R.drawable.logo_110)
                .placeholder(R.drawable.logo_110)
                .into(photo);

        final LinearLayout read=(LinearLayout)dialogView.findViewById(R.id.read);
        final LinearLayout write=(LinearLayout)dialogView.findViewById(R.id.write);
        final LinearLayout dotrec=(LinearLayout)dialogView.findViewById(R.id.receiverdots);
        final LinearLayout dotsend=(LinearLayout)dialogView.findViewById(R.id.senderdots);
        final TextView text = (TextView) dialogView.findViewById(R.id.text);
        final TextView text2 = (TextView) dialogView.findViewById(R.id.text2);
        final TextView datetime = (TextView) dialogView.findViewById(R.id.datetime);
        final TextView datetime2 = (TextView) dialogView.findViewById(R.id.datetime2);
        final TextView writespace = (TextView) dialogView.findViewById(R.id.writespace);
        ImageView image=(ImageView) dialogView.findViewById(R.id.image);
        ImageView image2=(ImageView) dialogView.findViewById(R.id.image2);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {

            if(is_talking==1){
                dotsend.setVisibility(View.INVISIBLE);
            }else {
                dotsend.setVisibility(View.VISIBLE);
            }

            photoFrame.setVisibility(View.GONE);
            read.setVisibility(View.GONE);

            dotrec.setVisibility(View.GONE);
            writespace.setVisibility(View.VISIBLE);
            write.setVisibility(View.VISIBLE);
            text2.setText(message);
            text2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(text2.getText().length() > 0){
//                        Intent shareIntent = new Intent();
//                        shareIntent.setAction(Intent.ACTION_SEND);
//                        shareIntent.setType("text/plain");
//                        shareIntent.putExtra(Intent.EXTRA_TEXT, text2.getText().toString());
//                        startActivity(Intent.createChooser(shareIntent, "Share via"));
//                    }
                }
            });

            text2.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    // TODO Auto-generated method stub
//                    showEditTextAlert(key, text2, dialogView);
                    return false;
                }
            });

            datetime2.setText(DateFormat.format("MM/dd/yyyy hh:mm a",
                    Long.parseLong(time)));

            if(imageStr.length()>0){
                image2.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext())
                        .load(imageStr)
                        .error(R.drawable.noresult)
                        .placeholder(R.drawable.noresult)
                        .into(image2);
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                        intent.putExtra("image", imageStr);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) ContactUsActivity.this, image2, getString(R.string.transition));
                        startActivity(intent, options.toBundle());
                    }
                });
                text2.setVisibility(View.GONE);
                image2.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
//                        showDeleteFileAlert(key, dialogView);
                        return false;
                    }
                });
            }else {
                image2.setVisibility(View.GONE);
                text2.setVisibility(View.VISIBLE);
            }

            if(is_talking==0){
                is_talking=1; is_talkingR=0;
            }
        }
        else{
            if(is_talkingR==1){
                photoFrame.setVisibility(View.INVISIBLE);
                dotrec.setVisibility(View.INVISIBLE);
            }else {
                photoFrame.setVisibility(View.VISIBLE);
                dotrec.setVisibility(View.VISIBLE);
            }

//            if(contactsFrame.getVisibility() == View.VISIBLE){
//                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
//                contactsFrame.setAnimation(animation);
//                contactsFrame.setVisibility(View.GONE);
//                cancelButton.setVisibility(View.GONE);
//            }

//            if(userPhoto.getVisibility() == View.INVISIBLE)userPhoto.setVisibility(View.VISIBLE);

            read.setVisibility(View.VISIBLE);
            dotsend.setVisibility(View.GONE);

            writespace.setVisibility(View.GONE);
            write.setVisibility(View.GONE);

            text.setText(message);
            //        reference.removeValue();
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(text.getText().length()>0){
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.sharevia)));
                    }
                }
            });
            datetime.setText(DateFormat.format("MM/dd/yyyy hh:mm a",
                    Long.parseLong(time)));

            if(imageStr.length()>0){
                image.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext())
                        .load(imageStr)
                        .error(R.drawable.noresult)
                        .placeholder(R.drawable.noresult)
                        .into(image);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                        intent.putExtra("image", imageStr);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) ContactUsActivity.this, image, getString(R.string.transition));
                        startActivity(intent, options.toBundle());
                    }
                });
                text.setVisibility(View.GONE);
            }else {
                image.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
            }

            if(is_talkingR==0){
                is_talking=0; is_talkingR=1;
            }
        }

        layout.addView(dialogView);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startTalking=true;
            }
        }, 2000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //From here you can load the image however you need to, I recommend using the Glide library
                File file = new File(resultUri.getPath());
                imagePortion.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                image.setImageBitmap(bitmap);
                imageToDonwloadUrl(file.getPath());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    private void imageToDonwloadUrl(String path){
        progressBar.setVisibility(View.VISIBLE);
        final String[] url = {""};
        final Uri[] uri = {Uri.fromFile(new File(path))};
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://pixil-250708.appspot.com");
        StorageReference fileReference = firebaseStorage.getReference();

        UploadTask uploadTask = fileReference.child(uri[0].getLastPathSegment()+ ".jpg").putFile(uri[0]);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBar.setVisibility(View.GONE);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Log.d("IMAGE===>",imageStr);
                fileReference.child(uri[0].getLastPathSegment()+ ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("onSuccess: uri= ", uri.toString());
                        imageStr = uri.toString();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void thumbToDonwloadUrl(String path){
        final String[] url = {""};
        final Uri[] uri = {Uri.fromFile(new File(path))};
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://pixil-250708.appspot.com");
        StorageReference fileReference = firebaseStorage.getReference();

        UploadTask uploadTask = fileReference.child(uri[0].getLastPathSegment()+ ".jpg").putFile(uri[0]);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUri = taskSnapshot.getUploadSessionUri();
                Log.d("ImageUrl===>", downloadUri.toString());
                videoThumbStr = downloadUri.toString();
            }
        });
    }

    private String saveImage(Bitmap finalBitmap, String image_name) {
        String path = "";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            path = file.getPath();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    public void uploadImage(){

        if(imageStr.length()>0){

            Map<String, String> map = new HashMap<String, String>();
            map.put("message", "");
            map.put("time", String.valueOf(new Date().getTime()));
            map.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
            map.put("name", Commons.thisUser.getName());
            map.put("image", imageStr);

            online("true");
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            is_typing=false;

            Map<String, String> map2 = new HashMap<String, String>();
            map2.put("message", getString(R.string.sharedpicture));
            map2.put("time", String.valueOf(new Date().getTime()));
            map2.put("sender_id", String.valueOf(Commons.thisUser.getIdx()));
            map2.put("sender_phone", Commons.thisUser.getPhoneNumber());
            map2.put("sender_name", Commons.thisUser.getName());
            reference3.removeValue();
            reference3.push().setValue(map2);

            sendFcmNotification(getString(R.string.sentpicture));

            imageStr="";
        }
    }

    public void sendFcmNotification(String message) {

    }

    private void getAdminData(){
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.post(ReqConst.SERVER_URL + "getAdminData")
                .addBodyParameter("authorization", "pixil.mn")
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

                                JSONArray objArr = response.getJSONArray("admins");
                                for(int i=0; i<objArr.length(); i++){
                                    JSONObject object = objArr.getJSONObject(i);
                                    User user = new User();
                                    user.setName(object.getString("firstname") + " " + object.getString("lastname"));
                                    user.setPhoto(object.getString("avatar"));

                                    if(i == 0){
                                        Glide.with(getApplicationContext())
                                                .load(user.getPhoto())
                                                .into(((CircleImageView)findViewById(R.id.staffimage1)));
                                        ((TextView)findViewById(R.id.staffname1)).setText(user.getName());
                                    }else if(i == 1){
                                        Glide.with(getApplicationContext())
                                                .load(user.getPhoto())
                                                .into(((CircleImageView)findViewById(R.id.staffimage2)));
                                        ((TextView)findViewById(R.id.staffname2)).setText(user.getName());
                                    }
                                }

                                if(contactsFrame.getVisibility() == View.GONE){
                                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                    contactsFrame.setAnimation(animation);
                                    contactsFrame.setVisibility(View.VISIBLE);
                                }
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


}








































