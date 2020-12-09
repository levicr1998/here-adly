package com.here.adly.services;

import com.here.adly.models.FeatureCollection;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIServiceHERE {

    @GET("spaces/bXsuXVfP/iterate")
    Call <FeatureCollection> getFeatures();
}
