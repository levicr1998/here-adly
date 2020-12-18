package com.here.adly.services;

import com.here.adly.models.FeatureCollection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIServiceHERE {

    @GET("spaces/{spaceId}/iterate")
    Call <FeatureCollection> getFeatures(@Path("spaceId") String spaceId);
}