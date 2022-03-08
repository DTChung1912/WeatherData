package com.example.weatherdata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherdata.Model.Example;
import com.example.weatherdata.Model.Main;
import com.example.weatherdata.Model.Weather;
import com.example.weatherdata.Model.Wind;
import com.example.weatherdata.Service.WeatherAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    TextView txtWeather, txtDay , txtDate,
            txtTemperature, txtCity, txtWind, txtHumidity;
    EditText edtCity;

    ImageView imgWeather;
    Button btnsend;
//    String url = "api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
    String apikey = "6c98c58f4f18a33fad6d42cda6107bc1";

    SimpleDateFormat dfDay, dfDate;

    List<Weather> weatherList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDay = findViewById(R.id.textViewDay);
        txtDate = findViewById(R.id.textViewDate);
        txtWeather = findViewById(R.id.textViewWeather);
        txtTemperature = findViewById(R.id.textViewTemperature);
        txtCity = findViewById(R.id.textViewCity);
        txtWind = findViewById(R.id.textViewWind);
        txtHumidity = findViewById(R.id.textViewHumidity);

        edtCity = findViewById(R.id.editTextCity);
        btnsend = findViewById(R.id.buttonSend);

        imgWeather = findViewById(R.id.imageViewWeather);

        dfDay = new SimpleDateFormat("EEE");
        dfDate = new SimpleDateFormat("d/MM/yyyy, HH:mm");

        txtDay.setText(dfDay.format(Calendar.getInstance().getTime())+"day");
        txtDate.setText(dfDate.format(Calendar.getInstance().getTime())+"");

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWeather(v);
            }
        });

    }

    public void getWeather(View v){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherAPI myApi = retrofit.create(WeatherAPI.class);
        Call<Example> exampleCall = myApi.getweather(edtCity.getText().toString().trim(),apikey);
        exampleCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.code()==404){
                    Toast.makeText(MainActivity.this,"Please Enter a valid City",Toast.LENGTH_LONG).show();
                }
                else if(!(response.isSuccessful())){
                    Toast.makeText(MainActivity.this,response.code()+" ",Toast.LENGTH_LONG).show();
                    return;
                }
                Example myData = response.body();

                // Temperature
                Main main = myData.getMain();
                Double temp = main.getTemp();
                Integer temperature = (int) (temp - 273.15);
                txtTemperature.setText(temperature+".");
                // City
                String cityName = myData.getName();
                txtCity.setText(cityName+"");
                // Humidity
                int humidity = main.getHumidity();
                txtHumidity.setText(humidity+"%");
                // Wind
                Wind wind = myData.getWind();
                Double speed = wind.getSpeed();
                Integer wind_degree = wind.getDeg();

                String derection = "";
                if (wind_degree == 0 ){
                    derection = "north";
                }else if (0< wind_degree && wind_degree < 45 ){
                    derection = "north-northeast";
                }else if (wind_degree == 45 ){
                    derection = "northeast";
                }else if (45< wind_degree && wind_degree < 90 ){
                    derection = "east-northeast";
                }else if (wind_degree == 90 ){
                    derection = "east";
                }else if (90< wind_degree && wind_degree < 135 ){
                    derection = "east-southeast ";
                }else if ( wind_degree == 135 ){
                    derection = "southeast ";
                }else if (135< wind_degree && wind_degree < 180 ){
                    derection = "south-southeast  ";
                }else if (wind_degree == 180 ){
                    derection = "south";
                }else if (180< wind_degree && wind_degree < 225 ){
                    derection = "south-southwest ";
                }else if (wind_degree == 225 ){
                    derection = "southwest ";
                }else if (225< wind_degree && wind_degree < 270 ){
                    derection = "west-southwest ";
                }else if (wind_degree == 270 ){
                    derection = "west";
                }else if (270< wind_degree && wind_degree < 315 ){
                    derection = "west-northwest ";
                }else if (wind_degree == 315 ){
                    derection = "northwest  ";
                }else {
                    derection = "north-northwest";
                }
                txtWind.setText(speed+" mph " +wind_degree +" - "+ derection );

                //Weather
//                weatherList = myData.getWeather();
                weatherList.addAll(myData.getWeather());
                String description = weatherList.get(0).getDescription();
                txtWeather.setText(description+"");

                // Weather icon
                String icon = weatherList.get(0).getIcon();
                String url = "http://openweathermap.org/img/wn/"+icon+"@2x.png";
                Glide.with(MainActivity.this).load(url).into(imgWeather);
            }
            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }}