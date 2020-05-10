package com.example.buddyapp.Interface;

import com.example.buddyapp.Model.Category;

import java.util.List;

public interface IServiceLoadListener {
        void onServiceLoadSuccess(List<Category> nameList, List<String> items);
        void onServiceLoadFailed(String message);
}
