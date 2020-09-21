package com.ekspeace.buddyapp.Interface;

import com.ekspeace.buddyapp.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlots);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
