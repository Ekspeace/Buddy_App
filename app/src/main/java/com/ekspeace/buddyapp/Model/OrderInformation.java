package com.ekspeace.buddyapp.Model;

import com.google.firebase.Timestamp;

public class OrderInformation {
    private String categoryname,BookingId,servicename,serviceId;
    private String customerPhone,customerName;
    private String time;
    private String date;
    private String otherType;
    private String otherInfo;
    private String groceryStore;
    private String groceryList;
    private String customerAddress;
    private String customerEmail;
    private String cannabisName;
    private String cannabisPrice;
    private String orderId;

    private Timestamp timestamp;
    private boolean done;

    public OrderInformation() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderInformation(String servicename) {
        this.servicename = servicename;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getGroceryStore() {
        return groceryStore;
    }

    public void setGroceryStore(String groceryStore) {
        this.groceryStore = groceryStore;
    }

    public String getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(String groceryList) {
        this.groceryList = groceryList;
    }

    public String getCannabisName() {
        return cannabisName;
    }

    public void setCannabisName(String cannabisName) {
        this.cannabisName = cannabisName;
    }

    public String getCannabisPrice() {
        return cannabisPrice;
    }

    public void setCannabisPrice(String cannabisPrice) {
        this.cannabisPrice = cannabisPrice;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
