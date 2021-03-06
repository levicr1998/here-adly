package com.here.adly.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feature {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("properties")
    @Expose
    private Properties properties;

    private String spaceId;

    public static final String SPACE_NAME_EUROPANEL = "bXsuXVfP";
    public static final String SPACE_NAME_TWOSIGN = "t5tgnuZA";
    public static final String SPACE_NAME_ABRI = "OA2v5p9Z";

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
