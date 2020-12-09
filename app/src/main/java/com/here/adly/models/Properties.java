package com.here.adly.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("@ns:com:here:xyz")
    @Expose
    private NsComHereXyz nsComHereXyz;
    @SerializedName("amenity")
    @Expose
    private String amenity;
    @SerializedName("capacity")
    @Expose
    private Integer capacity;
    @SerializedName("description")
    @Expose
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NsComHereXyz getNsComHereXyz() {
        return nsComHereXyz;
    }

    public void setNsComHereXyz(NsComHereXyz nsComHereXyz) {
        this.nsComHereXyz = nsComHereXyz;
    }

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
