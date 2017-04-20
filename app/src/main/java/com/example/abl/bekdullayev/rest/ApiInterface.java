package com.example.abl.bekdullayev.rest;

import com.example.abl.bekdullayev.model.ArticlesResponse;
import com.example.abl.bekdullayev.model.SourcesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("articles")
    Call<ArticlesResponse> getLatestNews(@Query("source") String source, @Query("sortBy") String sortBy, @Query("apiKey") String apiKey);
    @GET("sources")
    Call<SourcesResponse> getAvailableSources(@Query("apiKey") String apiKey);
}
