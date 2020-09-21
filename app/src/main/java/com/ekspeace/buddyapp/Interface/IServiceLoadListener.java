package com.ekspeace.buddyapp.Interface;

import com.ekspeace.buddyapp.Model.Category;

import java.util.List;

public interface IServiceLoadListener {
        void onServiceLoadSuccess(List<Category> nameList, List<String> items);
        void onServiceLoadFailed(String message);
}
