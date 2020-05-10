package com.example.buddyapp.Interface;

import com.example.buddyapp.Model.BookingInformation;

import java.util.List;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(List<BookingInformation> bookingInformation);
    void onBookingInfoLoadFailed(String message);
}
