package com.ekspeace.buddyapp.Interface;

import com.ekspeace.buddyapp.Model.Services;

import java.util.List;

public interface ICategoryLoadListener {
    void onCategoryLoadSuccess(List<Services> serviceList);
    void onCategoryLoadFailed(String message);
}
