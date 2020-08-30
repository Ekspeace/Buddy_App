package com.buddy.buddyapp.Interface;

import com.buddy.buddyapp.Model.Services;

import java.util.List;

public interface ICategoryLoadListener {
    void onCategoryLoadSuccess(List<Services> serviceList);
    void onCategoryLoadFailed(String message);
}
