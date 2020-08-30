package com.buddy.buddyapp.Interface;

import com.buddy.buddyapp.Model.OrderInformation;

import java.util.List;

public interface IOrderInfoLoadListener {
    void onOrderInfoLoadSuccess(List<OrderInformation> orderInformation);
    void onOrderInfoLoadFailed(String message);
}
