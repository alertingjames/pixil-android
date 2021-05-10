package com.app.pixil.commons;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.app.pixil.main.SelectPhotosActivity;
import com.app.pixil.main.StylePhotosActivity;
import com.app.pixil.models.Address;
import com.app.pixil.models.Image;
import com.app.pixil.models.ImageStyle;
import com.app.pixil.models.Order;
import com.app.pixil.models.User;

import java.util.ArrayList;

public class Commons {
    public static ArrayList<Image> selectedImages = new ArrayList<>();
    public static ArrayList<String> selectedImagePaths = new ArrayList<>();
    public static ArrayList<Image> orderedImages = new ArrayList<>();
    public static ArrayList<ImageStyle> imageStyles = new ArrayList<>();
    public static Image image = new Image();
    public static StylePhotosActivity stylePhotosActivity = null;
    public static SelectPhotosActivity selectPhotosActivity = null;
    public static boolean isLogged = false;

    public static User thisUser = null;
    public static User user = null;

    public static Bitmap bitmap = null;
    public static ImageView imageView = null;
    public static ArrayList<String> photoUrls = new ArrayList<>();

    public static Address address = null;
    public static ArrayList<Address> addressList = new ArrayList<>();
    public static Order order = new Order();

    public static String lang = "en";

}
