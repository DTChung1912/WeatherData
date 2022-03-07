package com.example.weatherdata.Service;

import com.example.weatherdata.Model.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {
    @GET("weather")
    Call<Example> getweather(@Query("q") String cityname,
                             @Query("appid") String apikey);
}
