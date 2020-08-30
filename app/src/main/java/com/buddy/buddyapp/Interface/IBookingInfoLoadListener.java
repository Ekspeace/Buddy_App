package com.buddy.buddyapp.Interface;

import com.buddy.buddyapp.Model.BookingInformation;

import java.util.List;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(List<BookingInformation> bookingInformation);
    void onBookingInfoLoadFailed(String message);
}
