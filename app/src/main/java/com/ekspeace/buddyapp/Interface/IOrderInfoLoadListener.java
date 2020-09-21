package com.ekspeace.buddyapp.Interface;

import com.ekspeace.buddyapp.Model.OrderInformation;

import java.util.List;

public interface IOrderInfoLoadListener {
    void onOrderInfoLoadSuccess(List<OrderInformation> orderInformation);
    void onOrderInfoLoadFailed(String message);
}
