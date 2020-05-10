package com.example.buddyapp.Interface;

import com.example.buddyapp.Model.Services;

import java.util.List;

public interface ICategoryLoadListener {
    void onCategoryLoadSuccess(List<Services> serviceList);
    void onCategoryLoadFailed(String message);
}
