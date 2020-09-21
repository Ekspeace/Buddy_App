package com.ekspeace.buddyapp.Model;

public class Services {

    private  String name,serviceId, imageUrl;
    public Services() {
    }

    public Services(String name, String serviceId, String imageUrl) {
        this.name = name;
        this.serviceId = serviceId;
        this.imageUrl = imageUrl;
    }
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

}
