package com.here.adly.viewmodels;

public class FavItemViewModel {


    private String spaceId;
    private String adName;
    private String adId;

    public FavItemViewModel() {

    }

    public FavItemViewModel(String adName, String spaceId) {
        this.adName = adName;
        this.spaceId = spaceId;
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

    public String getSpaceId() {
        return spaceId;
    }
}
