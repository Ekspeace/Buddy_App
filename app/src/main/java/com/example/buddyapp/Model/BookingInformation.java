package com.example.buddyapp.Model;


import com.google.firebase.Timestamp;

public class BookingInformation {
    private String categoryname,BookingId,servicename,serviceId;

    private String customerName;
    private String customerPhone;
    private String time;
    private String customerAddress;
    private String customerEmail;
    private Long slot;
    private Timestamp timestamp;
    private boolean done;

    public BookingInformation() {
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getCategoryName() {
        return categoryname;
    }

    public void setCategoryName(String categoryname) {
        this.categoryname = categoryname;
    }


    public String getServiceName() {
        return servicename;
    }

    public void setServiceName(String servicename) {
        this.servicename = servicename;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
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



    public BookingInformation(String servicename, Long slot, Timestamp timestamp) {
        this.servicename = servicename;
        this.slot = slot;
        this.timestamp = timestamp;
    }

    public BookingInformation(String categoryname, String servicename, String serviceId, String customerName, String customerPhone, String time, String customerAddress, String customerEmail, Long slot, Timestamp timestamp, boolean done) {
        this.categoryname = categoryname;
        this.servicename = servicename;
        this.serviceId = serviceId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.customerAddress = customerAddress;
        this.customerEmail = customerEmail;
        this.slot = slot;
        this.timestamp = timestamp;
        this.done = done;
    }
}
