package com.example.weatherapi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etCity;
    private Button checkWeatherBtn, clearBtn;
    private TextView tvTemp, tvMinTemp, tvMaxTemp, tvHumidity, tvWeather, tvDescription;

    private final String API_KEY = "Insert OpenWeatherMap API Key here.";
    private final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initializeViews();
        requestQueue = Volley.newRequestQueue(this);
        setUpClickListeners();
    }
    
    private void initializeViews() {
        etCity = findViewById(R.id.editTextCity);
        checkWeatherBtn = findViewById(R.id.checkWeatherBtn);
        clearBtn = findViewById(R.id.clearBtn);
        tvTemp = findViewById(R.id.textViewTemp);
        tvMinTemp = findViewById(R.id.textViewMinTemp);
        tvMaxTemp = findViewById(R.id.textViewMaxTemp);
        tvHumidity = findViewById(R.id.textViewHum);
        tvWeather = findViewById(R.id.textViewWeather);
        tvDescription = findViewById(R.id.textViewDesc);
    }
    
    private void setUpClickListeners() {
        checkWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });
    }
    
    private void getWeatherData(String city) {
        String url = String.format(API_URL, city, API_KEY);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject mainObj = response.getJSONObject("main") ;
                            double temp = mainObj.getDouble("temp");
                            double minTemp = mainObj.getDouble("temp_min");
                            double maxTemp = mainObj.getDouble("temp_max");
                            int humidity = mainObj.getInt("humidity");

                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weatherObj = weatherArray.getJSONObject(0);
                            String weather = weatherObj.getString("main");
                            String description = weatherObj.getString("description");

                            // Display ng data
                            tvTemp.setText("TEMPERATURE: " + String.format("%.1f", temp) + "°C");
                            tvMinTemp.setText("MINIMUM TEMPERATURE: " + String.format("%.1f", minTemp) + "°C");
                            tvMaxTemp.setText("MAXIMUM TEMPERATURE: " + String.format("%.1f", maxTemp) + "°C");
                            tvHumidity.setText("HUMIDITY: " + humidity + "%");
                            tvWeather.setText("WEATHER: " + weather);
                            tvDescription.setText("DESCRIPTION: " + description);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error fetching weather data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void clearData() {
        etCity.setText("");
        tvTemp.setText("TEMPERATURE");
        tvMinTemp.setText("MINIMUM TEMPERATURE");
        tvMaxTemp.setText("MAXIMUM TEMPERATURE");
        tvHumidity.setText("HUMIDITY");
        tvWeather.setText("WEATHER");
        tvDescription.setText("DESCRIPTION");
    }
}
