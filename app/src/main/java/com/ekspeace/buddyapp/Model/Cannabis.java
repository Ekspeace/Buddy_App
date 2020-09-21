package com.ekspeace.buddyapp.Model;

public class Cannabis {
  private String CannabisName, CannabisPrice;

    public Cannabis(String cannabisName, String cannabisPrice) {
        CannabisName = cannabisName;
        CannabisPrice = cannabisPrice;
    }

    public Cannabis() {
    }

    public String getCannabisName() {
        return CannabisName;
    }

    public void setCannabisName(String cannabisName) {
        CannabisName = cannabisName;
    }

    public String getCannabisPrice() {
        return CannabisPrice;
    }

    public void setCannabisPrice(String cannabisPrice) {
        CannabisPrice = cannabisPrice;
    }
}
