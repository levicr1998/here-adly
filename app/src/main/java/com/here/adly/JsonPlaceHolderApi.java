package com.here.adly;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {

    @GET("spaces/bXsuXVfP/iterate")
    Call <FeatureCollection> getFeatures();
}
