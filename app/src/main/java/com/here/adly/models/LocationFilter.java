package com.here.adly.models;

public class LocationFilter extends Filter {

    private String selectedPostalCode;
    private int selectedKiloMetersNearMe;

    public LocationFilter(String name, String selectedPostalCode, int selectedKiloMetersNearMe) {
        super(name);
        this.selectedPostalCode = selectedPostalCode;
        this.selectedKiloMetersNearMe = selectedKiloMetersNearMe;


    }

    public String getSelectedPostalCode() {
        return selectedPostalCode;
    }

    public void setSelectedPostalCode(String selectedPostalCode) {
        this.selectedPostalCode = selectedPostalCode;
    }

    public int getSelectedKiloMetersNearMe() {
        return selectedKiloMetersNearMe;
    }

    public void setSelectedKiloMetersNearMe(int selectedKiloMetersNearMe) {
        this.selectedKiloMetersNearMe = selectedKiloMetersNearMe;
    }
}
