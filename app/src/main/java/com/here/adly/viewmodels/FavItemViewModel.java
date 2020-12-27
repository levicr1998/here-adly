package com.here.adly.viewmodels;

public class FavItemViewModel {


    private String spaceId;
    private String adId;
    private String adName;
    private boolean status;

    public FavItemViewModel(){

    }

    public FavItemViewModel(String adId){
        this.adId = adId;
    }



    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getAdName() {
        return adName;
    }

    public boolean getStatus() {
        return status;
    }

    public String getSpaceId() {
        return spaceId;
    }
}
