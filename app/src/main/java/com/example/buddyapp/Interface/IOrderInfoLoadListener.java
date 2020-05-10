package com.example.buddyapp.Interface;

import com.example.buddyapp.Model.OrderInformation;

import java.util.List;

public interface IOrderInfoLoadListener {
    void onOrderInfoLoadSuccess(List<OrderInformation> orderInformation);
    void onOrderInfoLoadFailed(String message);
}
