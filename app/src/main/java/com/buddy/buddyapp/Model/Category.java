package com.buddy.buddyapp.Model;

public class Category {
    private String categoryName, categoryPrice;

    public Category() {
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryName, String categoryPrice) {
        this.categoryName = categoryName;
        this.categoryPrice = categoryPrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryPrice() {
        return categoryPrice;
    }

    public void setCategoryPrice(String categoryPrice) {
        this.categoryPrice = categoryPrice;
    }
}
