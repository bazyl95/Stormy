package com.millenial.stormy;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.millenial.stormy.databinding.ActivityMainBinding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather currentWeather;
    private ImageView iconImageView;

    final double latitude = 37.8267;
    final double longitude = -122.4233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getForecast(latitude,longitude);
    }

    private void getForecast(double latitude, double longitude) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);

        TextView darkSky = findViewById(R.id.darkSkyAttribution);
        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        iconImageView = findViewById(R.id.iconImageView);

        String apiKey = "1a31d24543d4c17a1664269fbdc8d411";

        String forecastURL = "https://api.darksky.net/forecast/"
                + apiKey  + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            currentWeather = getCurrentDetails(jsonData);

                            final CurrentWeather displayWeather = new CurrentWeather(
                                    currentWeather.getLocationLabel(),
                                    currentWeather.getIcon(),
                                    currentWeather.getTime(),
                                    currentWeather.getTemperature(),
                                    currentWeather.getHumidity(),
                                    currentWeather.getPrecipChance(),
                                    currentWeather.getSummary(),
                                    currentWeather.getTimeZone()
                            );

                            binding.setWeather(displayWeather);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Drawable drawable = getResources().getDrawable(displayWeather.getIconId());
                                    iconImageView.setImageDrawable(drawable);
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                    } catch (JSONException e){
                        Log.e(TAG, "JSON Exception caught: ", e);
                    }
                }
            });

        }
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);

        String timeZone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timeZone);

        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();

        // Setting values from JSON to CurrentWeather Data Module.

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setLocationLabel("Alcatraz Island, CA");
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTimeZone(timeZone);

        Log.d(TAG, currentWeather.getFormattedTime());
        //Log.i(TAG, "From inside of JSON: " + currently.getString("summary"));

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        else {
            Toast.makeText(this, R.string.network_unavailable_message,
                    Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }

    public void refreshOnClick(View view){
        getForecast(latitude, longitude);
        Toast.makeText(this, "Refreshing data", Toast.LENGTH_LONG).show();
    }
}
