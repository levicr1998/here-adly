package com.here.adly.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NsComHereXyz {
    @SerializedName("createdAt")
    @Expose
    private float createdAt;
    @SerializedName("updatedAt")
    @Expose
    private float updatedAt;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("space")
    @Expose
    private String space;

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public float getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(float createdAt) {
        this.createdAt = createdAt;
    }

    public float getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(float updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
