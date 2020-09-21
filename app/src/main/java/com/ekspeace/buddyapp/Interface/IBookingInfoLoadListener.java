package com.ekspeace.buddyapp.Interface;

import com.ekspeace.buddyapp.Model.BookingInformation;

import java.util.List;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(List<BookingInformation> bookingInformation);
    void onBookingInfoLoadFailed(String message);
}
