package com.ekspeace.buddyapp.Model;


import com.google.firebase.Timestamp;

public class BookingInformation {
    private String categoryName,BookingId, serviceName,serviceId;
    private String customerName;
    private String customerPhone;
    private String time;
    private String customerAddress;
    private String customerEmail;
    private Long slot;
    private Timestamp timestamp;
    private boolean done;
    private String Price;

    public BookingInformation() {
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBookingId() {
        return BookingId;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryname) {
        this.categoryName = categoryname;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String servicename) {
        this.serviceName = servicename;
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



    public BookingInformation(String serviceName, Long slot, Timestamp timestamp) {
        this.serviceName = serviceName;
        this.slot = slot;
        this.timestamp = timestamp;
    }

    public BookingInformation(String categoryName, String serviceName, String serviceId, String customerName, String customerPhone, String time, String customerAddress, String customerEmail, Long slot, Timestamp timestamp, boolean done, String Price) {
        this.categoryName = categoryName;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.customerAddress = customerAddress;
        this.customerEmail = customerEmail;
        this.slot = slot;
        this.timestamp = timestamp;
        this.done = done;
        this.Price = Price;
    }
}
