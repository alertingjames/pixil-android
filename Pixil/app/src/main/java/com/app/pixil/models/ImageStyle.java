package com.app.pixil.models;

public class ImageStyle {
    int idx = 0;
    String frame = "";
    String title = "";
    String title_mong = "";
    int photoWidth = 0;
    int photoHeight = 0;
    int frameWidth = 0;
    int frameHeight = 0;
    float price = 0.0f;
    String rightSide = "";
    String bottomSide = "";
    int productWidth = 20;
    int productHeight = 20;
    int frameRes = 0;

    public ImageStyle(){

    }

    public void setTitle_mong(String title_mong) {
        this.title_mong = title_mong;
    }

    public void setPhotoWidth(int photoWidth) {
        this.photoWidth = photoWidth;
    }

    public void setPhotoHeight(int photoHeight) {
        this.photoHeight = photoHeight;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setRightSide(String rightSide) {
        this.rightSide = rightSide;
    }

    public void setBottomSide(String bottomSide) {
        this.bottomSide = bottomSide;
    }

    public void setProductWidth(int productWidth) {
        this.productWidth = productWidth;
    }

    public void setProductHeight(int productHeight) {
        this.productHeight = productHeight;
    }

    public String getTitle_mong() {
        return title_mong;
    }

    public int getPhotoWidth() {
        return photoWidth;
    }

    public int getPhotoHeight() {
        return photoHeight;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public float getPrice() {
        return price;
    }

    public String getRightSide() {
        return rightSide;
    }

    public String getBottomSide() {
        return bottomSide;
    }

    public int getProductWidth() {
        return productWidth;
    }

    public int getProductHeight() {
        return productHeight;
    }

    public void setFrameRes(int frameRes) {
        this.frameRes = frameRes;
    }

    public int getFrameRes() {
        return frameRes;
    }

    public int getIdx() {
        return idx;
    }

    public String getFrame() {
        return frame;
    }

    public String getTitle() {
        return title;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
