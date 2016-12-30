package com.duy.aahomeinternet.weather;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duy.aahomeinternet.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by drthanh on 11/05/2015.
 */
public class WeatherAsyncTask extends AsyncTask<Void, Void, OpenWeatherJSon> {

    Activity activity;
    TypePrediction typePrediction;
    String q;
    double latitude;
    double longitude;
    NumberFormat format = new DecimalFormat("#0.0");
    Bitmap myBitmap = null;

    /**
     * Constructor dùng để lấy thời tiết theo địa chỉ bất kỳ
     *
     * @param activity
     * @param q
     */
    public WeatherAsyncTask(Activity activity, String q) {
        this.activity = activity;
        this.typePrediction = TypePrediction.ADDRESS_NAME;
        this.q = q;
    }

    /**
     * constructor cho phép lấy thông tin thời tiết theo tọa độ bất kỳ
     *
     * @param activity
     * @param latitude
     * @param longitude
     */
    public WeatherAsyncTask(Activity activity, double latitude, double longitude) {
        this.activity = activity;
        this.typePrediction = TypePrediction.LATITUDE_LONGITUDE;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    @Override
    protected OpenWeatherJSon doInBackground(Void... params) {
        OpenWeatherJSon openWeatherJSon = null;
        if (typePrediction == TypePrediction.LATITUDE_LONGITUDE)
            openWeatherJSon = OpenWeatherMapAPI.prediction(latitude, longitude);
        else
            openWeatherJSon = OpenWeatherMapAPI.prediction(q);
        try {
            String idIcon = openWeatherJSon.getWeather().get(0).getIcon().toString();
            String urlIcon = "http://openweathermap.org/img/w/" + idIcon + ".png";
//Tiến hành tạo đối tượng URL
            URL urlConnection = new URL(urlIcon);
//Mở kết nối
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
//Đọc dữ liệu
            InputStream input = connection.getInputStream();
//Tiến hành convert qua hình ảnh
            myBitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openWeatherJSon;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(OpenWeatherJSon openWeatherJSon) {
        super.onPostExecute(openWeatherJSon);
        View view = activity.findViewById(R.id.progressBar);
        if (view != null) view.setVisibility(View.GONE);

        View view2 = activity.findViewById(R.id.content_weather);
        if (view2 != null) view2.setVisibility(View.VISIBLE);

        TextView txtTemperature = (TextView) activity.findViewById(R.id.txtTemperature);
        ImageView imageView = (ImageView) activity.findViewById(R.id.imgBauTroi);
        TextView txtWind = (TextView) activity.findViewById(R.id.txtWind);
        TextView txtCloudliness = (TextView) activity.findViewById(R.id.txtCloudliness);
        TextView txtPressure = (TextView) activity.findViewById(R.id.txtPressure);
        TextView txtHumidty = (TextView) activity.findViewById(R.id.txtHumidty);
        double temperature = openWeatherJSon.getMain().getTemp() - 273.15;
        String wind = openWeatherJSon.getWind().getSpeed() + " m/s";
        String mesg = openWeatherJSon.getWeather().get(0).getMain();
        String cloudiness = mesg;
        String pressure = openWeatherJSon.getMain().getPressure() + " hpa";
        String humidity = openWeatherJSon.getMain().getHumidity() + " %";

        txtTemperature.setText(format.format(temperature) + "°C");
        imageView.setImageBitmap(myBitmap);
        txtWind.setText(wind);
        txtCloudliness.setText(cloudiness);
        txtPressure.setText(pressure);
        txtHumidty.setText(humidity);
    }
}