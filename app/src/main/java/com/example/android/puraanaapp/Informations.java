package com.example.android.puraanaapp;

public class Informations {

    public String product;
    public String provider;
    public String price;
    public String photoUrl;
    public String phone;

    public Informations() {
    }

    public Informations(String product, String provider, String price, String phone, String photoUrl) {
        this.product = product;
        this.provider = provider;
        this.price = price;
        this.phone = phone;
        this.photoUrl = photoUrl;
    }

    public String getPhotoUrl(){return photoUrl;}

    public String getProduct(){return product;}

    public String getProvider(){return provider;}

    public String getPrice(){return price;}

    public String getPhone(){return phone;}

}
