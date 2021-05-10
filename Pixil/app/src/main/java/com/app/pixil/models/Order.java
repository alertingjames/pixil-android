package com.app.pixil.models;

import java.util.ArrayList;

public class Order {
    int id = 0;
    int customerId = 0;
    String email = "";
    int pixils = 0;
    String fullName = "";
    String phoneNumber = "";
    String address = "";
    String district = "";
    String city = "";
    String zipCode = "";
    String province = "";
    String deliveryDate = "";
    String trackingNumber = "";
    float pixilPrice = 0.0f;
    float additionalPrice = 0.0f;
    String status = "";
    String orderDate = "";
    String printDate = "";

    ArrayList<String> pictures = new ArrayList<>();

    public Order(){}

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPixils(int pixils) {
        this.pixils = pixils;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setPixilPrice(float pixilPrice) {
        this.pixilPrice = pixilPrice;
    }

    public void setAdditionalPrice(float additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setPrintDate(String printDate) {
        this.printDate = printDate;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures.clear();
        this.pictures.addAll(pictures);
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public int getPixils() {
        return pixils;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDistrict() {
        return district;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getProvince() {
        return province;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public float getPixilPrice() {
        return pixilPrice;
    }

    public float getAdditionalPrice() {
        return additionalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getPrintDate() {
        return printDate;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }
}