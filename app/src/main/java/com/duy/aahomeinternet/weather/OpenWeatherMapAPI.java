package com.duy.aahomeinternet.weather;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by drthanh on 03/04/2015.
 */
public class OpenWeatherMapAPI {
    private static final String KEY = "&appid=dbeff8996000e955e5beef14c8249dde";

    public static OpenWeatherJSon prediction(String q) {
        try {
            String location = URLEncoder.encode(q, "UTF-8");

            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + location + KEY);
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            OpenWeatherJSon results = new Gson().fromJson(reader, OpenWeatherJSon.class);

            String idIcon = results.getWeather().get(0).getIcon().toString();
            String urlIcon = "http://openweathermap.org/img/w/" + idIcon + ".png";
            URL urlImage = new URL(urlIcon);

            return results;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * http://api.openweathermap.org/data/2.5/weather?lat=10.778182&amp;lon=106.665504
     *
     * @param lat
     * @param lon
     * @return
     */
    public static OpenWeatherJSon prediction(double lat, double lon) {
        try {

            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&amp;lon=" + lon + KEY);
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            OpenWeatherJSon results = new Gson().fromJson(reader, OpenWeatherJSon.class);

            String idIcon = results.getWeather().get(0).getIcon().toString();
            String urlIcon = "http://openweathermap.org/img/w/" + idIcon + ".png";
            URL urlImage = new URL(urlIcon);

            return results;

        } catch (MalformedURLException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
        }
        return null;
    }

    /**
     * Sửa lại WeatherJSON vì chưa phù hợp trong trường hợp Daily
     * http://api.openweathermap.org/data/2.5/forecast/daily?lat=10.778182&amp;lon=106.66550&amp;cnt=10
     *
     * @param lat
     * @param lon
     * @param cnt
     * @return
     */
    public static OpenWeatherJSon predictionDaily(double lat, double lon, int cnt) {
        try {

            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + lat + "&amp;lon=" + lon + "&amp;cnt=" + cnt + KEY);
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            OpenWeatherJSon results = new Gson().fromJson(reader, OpenWeatherJSon.class);

            String idIcon = results.getWeather().get(0).getIcon().toString();
            String urlIcon = "http://openweathermap.org/img/w/" + idIcon + ".png";
            URL urlImage = new URL(urlIcon);

            return results;

        } catch (MalformedURLException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
        }
        return null;
    }

    /**
     * Sửa lại WeatherJSON vì chưa phù hợp trong trường hợp Daily
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=Đà lạt&amp;cnt=10
     *
     * @param q
     * @param cnt
     * @return
     */
    public static OpenWeatherJSon predictionDaily(String q, int cnt) {
        try {
            String location = URLEncoder.encode(q, "UTF-8");
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + location + "&amp;cnt=" + cnt + KEY);
            InputStreamReader reader = new InputStreamReader(url.openStream(), "UTF-8");
            OpenWeatherJSon results = new Gson().fromJson(reader, OpenWeatherJSon.class);

            String idIcon = results.getWeather().get(0).getIcon().toString();
            String urlIcon = "http://openweathermap.org/img/w/" + idIcon + ".png";
            URL urlImage = new URL(urlIcon);

            return results;

        } catch (MalformedURLException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
        }
        return null;
    }
}