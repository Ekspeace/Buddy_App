package com.example.buddyapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Services implements Parcelable {

    private  String name,serviceId, imageUrl;
    public Services() {
    }

    public Services(String name, String serviceId, String imageUrl) {
        this.name = name;
        this.serviceId = serviceId;
        this.imageUrl = imageUrl;
    }

    protected Services(Parcel in) {
        name = in.readString();
        serviceId = in.readString();
    }

    public static final Creator<Services> CREATOR = new Creator<Services>() {
        @Override
        public Services createFromParcel(Parcel in) {
            return new Services(in);
        }

        @Override
        public Services[] newArray(int size) {
            return new Services[size];
        }
    };

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

     public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(serviceId);
    }
}
