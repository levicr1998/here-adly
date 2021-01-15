package com.here.adly.webservices;

import com.here.adly.utils.TokenInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientHERE {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://xyz.api.here.com/hub/";

    public ClientHERE() {
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