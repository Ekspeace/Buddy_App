package com.example.buddyapp.Interface;

import com.example.buddyapp.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlots);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
