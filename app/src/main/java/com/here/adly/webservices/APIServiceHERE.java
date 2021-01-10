package com.here.adly.webservices;

import com.here.adly.models.Feature;
import com.here.adly.models.FeatureCollection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIServiceHERE {

    @GET("spaces/{spaceId}/iterate")
    Call<FeatureCollection> getFeatures(@Path("spaceId") String spaceId);

    @GET("spaces/{spaceId}/features/{featureId}")
    Call<Feature> getFeaturebyId(@Path("spaceId") String spaceId, @Path("featureId") String featureId);
}