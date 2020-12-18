package com.here.adly.services;

import com.here.adly.models.FeatureCollection;
import com.here.adly.utils.TokenInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeatureLocationCollectionManager {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://xyz.api.here.com/hub/";

    public FeatureLocationCollectionManager() {
    }


    public APIServiceHERE setupClient() {
        TokenInterceptor interceptor = new TokenInterceptor();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIServiceHERE APIServiceHERE = retrofit.create(APIServiceHERE.class);
        return APIServiceHERE;
    }
}