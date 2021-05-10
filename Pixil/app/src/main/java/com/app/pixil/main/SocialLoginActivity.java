package com.app.pixil.main;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.pixil.R;
import com.app.pixil.base.BaseActivity;
import com.app.pixil.classes.GDController;
import com.app.pixil.commons.Commons;
import com.app.pixil.commons.Constants;
import com.app.pixil.models.Album;
import com.app.pixil.models.User;
import com.app.pixil.utils.Util;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class SocialLoginActivity extends BaseActivity {

    LinearLayout googleButton, instagramButton, facebookButton;
    public static CallbackManager callbackManager;
    /** Instance of the Google Play controller */
    GDController gdController;
    public static int currentPage = 1;
    public static String TAG = "SocialLoginActivity";
    private String googleAuthToken;
    private String googleAccountName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();      ////////////////////////////

        // Get a reference to the Google Play controller
        gdController = GDController.getInstance();

        googleButton = (LinearLayout)findViewById(R.id.btn_google);
        instagramButton = (LinearLayout)findViewById(R.id.btn_instagram);
        facebookButton = (LinearLayout)findViewById(R.id.btn_facebook);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPhotos();
            }
        });

        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFB();
            }
        });

    }

    private void fetchPhotos() {
        currentPage = 1;
        gdController.getImageFeed().clear();

        if (Util.isDeviceOnline(getApplicationContext())) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    if(currentPage ==1 || (currentPage > 1 && gdController.isMoreAvailable())) {
                        ArrayList<GDController.GDModel> photoResults;
                        // get the photo search results
                        photoResults = gdController.getPhotos(currentPage);
                        Log.d(TAG, "SERVER RESPONSE!!!");
                        if (photoResults.size() > 0)
                            gdController.getImageFeed().addAll(photoResults);

                        currentPage++;
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (gdController.getImageFeed().size() > 0) {
//                        imageAdapter.notifyDataSetChanged();
//                        // Obtain current position to maintain scroll position
//                        int currentPosition = albumGrid.getFirstVisiblePosition();
//
//                        // Set new scroll position
//                        albumGrid.smoothScrollToPosition(currentPosition + 1, 0);
                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "No Internet connected", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            // Display a dialog showing the connection error
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                            connectionStatusCode, SocialLoginActivity.this,
                            Constants.REQUEST_GOOGLE_PLAY_SERVICES);
                    dialog.show();
                }
            });
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    private void chooseGoogleAccount() {

        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);

        Log.d(TAG,"Starting activity for Choosing Account");
        startActivityForResult(intent, Constants.REQUEST_ACCOUNT_PICKER);
    }

    private void fetchAuthToken() {
        if (googleAccountName != null) {
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(Constants.PREF_ACCOUNT_NAME, googleAccountName);
            editor.commit();

            if (Util.isDeviceOnline(getApplicationContext())) {
                new AsyncTask(){

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        try {
                            Log.d(TAG,"Requesting token for account: " +
                                    googleAccountName);
                            googleAuthToken = GoogleAuthUtil.getToken(getApplicationContext(),
                                    googleAccountName, Constants.GPHOTOS_SCOPE);

                            Log.d(TAG, "Received Token: " + googleAuthToken);
                            gdController.setAPIToken(googleAuthToken);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        } catch (UserRecoverableAuthException e) {
                            startActivityForResult(e.getIntent(), Constants.REQ_SIGN_IN_REQUIRED);
                        } catch (GoogleAuthException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        return null;
                    }
                }.execute();
            } else {
                Toast.makeText(this, "Device not online", Toast.LENGTH_LONG).show();
            }
        } else {
            chooseGoogleAccount();
        }
    }

    public void back(View view){
        onBackPressed();
    }

    private String FEmail, Name, Firstname, Lastname, Id, Gender="", Image_url, Birthday="";

    private void loginWithFB() {
        // set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());

                                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                                Log.d("IsLoggedIn???", String.valueOf(isLoggedIn));

                                Log.d("Login Token!!!", loginResult.getAccessToken().getToken());

                                try {

                                    User user = new User();

                                    Name = object.getString("name");
                                    Name.trim();
                                    Id = object.getString("id");
                                    Firstname = object.getString("first_name");
                                    Lastname = object.getString("last_name");
                                    FEmail = object.getString("email");
                                    Image_url = "http://graph.facebook.com/(Id)/picture?type=large";
                                    Image_url = URLEncoder.encode(Image_url);
                                    Log.d("Email = ", " " + FEmail);
                                    Log.d("Name======", Name);
                                    Log.d("Image====",Image_url.toString());
                                    Log.d("firstName======", Firstname);
                                    Log.d("lastName======", Lastname);
                                    Log.d("id======", Id);
                                    Log.d("Object=====>", object.toString());
                                    Log.d("photourl======", Image_url.toString());

                                    if (object.has("picture")) {
                                        JSONObject jsonPicture = object.getJSONObject("picture");
                                        if (jsonPicture.has("data")) {
                                            JSONObject jsonData = jsonPicture.getJSONObject("data");
                                            if (jsonData.has("url"))
                                                user.setPhoto(jsonData.getString("url"));
                                        }
                                    }

//                                    Gender = object.getString("gender");
//                                    Log.d("Gender======", Gender);
//
//                                    try{
//                                        Birthday = object.getString("birthday");
//                                        Log.d("Birthday: ",Birthday);
//                                    }catch (JSONException e){
//                                        e.printStackTrace();
//                                    }

//                                    JSONObject photosobject = object.getJSONObject("photos");     /////////////////////////////////////////////////////////////////////
//                                    Log.d("PHOTOS===>", photosobject.toString());

//                                    ArrayList<Album> Albums = new ArrayList<>();
//                                    try {
//                                        JSONArray albumsJSArray = object.getJSONObject("albums").getJSONArray("data");
//                                        getAlbumsArray(albumsJSArray, Albums, SocialLoginActivity.this);
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        showToast("This test user doesn't get user_photos permission.");
//                                    }

                                    user.setName(Firstname + " " + Lastname);
                                    user.setEmail(FEmail);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name, last_name, email, gender, birthday, picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();

            }

            @Override
            public void onError(FacebookException e) {
                Log.d("Facebook login error!!!", e.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case Constants.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case Constants.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    googleAccountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    fetchAuthToken();
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d("SocialLoginActivity","Google account unspecified");
                }
                break;
            case Constants.REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseGoogleAccount();
                }
                break;
            case Constants.REQ_SIGN_IN_REQUIRED:
                if(resultCode == RESULT_OK) {
                    // We had to sign in - now we can finish off the token request.
                    fetchAuthToken();
                }
        }
    }

    // Extract albums' data from JSON Arrays to Album ArrayList
    private void getAlbumsArray(JSONArray albumsJSArray, ArrayList<Album> Albums, Activity activity) {
        for (int i = 0; i < albumsJSArray.length(); i++) {
            //getting Albums names and id
            try {
                Albums.add(new Album(albumsJSArray.getJSONObject(i).getString("name"), albumsJSArray.getJSONObject(i).getString("id")));
                if (albumsJSArray.getJSONObject(i).has("photos")) {
                    JSONArray photos = albumsJSArray.getJSONObject(i).getJSONObject("photos").getJSONArray("data");
                    Log.d("LENGTH===>", String.valueOf(photos.length()));
                    for (int j = 0; j < photos.length(); j++) {
                        //getting Album photos
                        Albums.get(i).getPhotosUrls().add(j, photos.getJSONObject(j).getJSONArray("images").getJSONObject(0).getString("source"));
                        Log.d("PHOTOS===>", photos.getJSONObject(j).getJSONArray("images").getJSONObject(0).getString("source"));
                        Commons.photoUrls.add(photos.getJSONObject(j).getJSONArray("images").getJSONObject(0).getString("source"));
                    }
                } else {
                    //setting a default icon if the Album is empty
                    Albums.get(i).getPhotosUrls().add(activity.getApplicationContext().getResources().getString(R.string.default_album_cover));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}





















































