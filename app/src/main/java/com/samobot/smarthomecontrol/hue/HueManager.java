package com.samobot.smarthomecontrol.hue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HueManager {

    private String device_ip = null;
    private String apikey = null;

    public HueManager(String device_ip, String apikey) {
        this.device_ip = device_ip;
        this.apikey = apikey;
    }

    public void setLightState(int lightID, boolean state) {
        SetLightNetworkThread lightRequest = new SetLightNetworkThread(lightID, state);
        lightRequest.start();
    }

    public void setLightBrightness(int lightID, int brightness) {
        SetLightBrightnessNetworkThread lightRequest = new SetLightBrightnessNetworkThread(lightID, brightness);
        lightRequest.start();
    }

    public void setLightTemperature(int lightID, int temperature) {
        SetLightTemperatureNetworkThread lightRequest = new SetLightTemperatureNetworkThread(lightID, temperature);
        lightRequest.start();
    }

    public void getLightEnabled(int lightID, RunWhenDoneState runWhenDone) {
        GetLightEnabledNetworkThread lightRequest = new GetLightEnabledNetworkThread(lightID, runWhenDone);
        lightRequest.start();
    }

    public void getLightBrightness(int lightID, RunWhenDoneBrightness runWhenDone) {
        GetLightBrightnessNetworkThread lightRequest = new GetLightBrightnessNetworkThread(lightID, runWhenDone);
        lightRequest.start();
    }

    public void getLightTemperature(int lightID, RunWhenDoneTemperature runWhenDone) {
        GetLightTemperatureNetworkThread lightRequest = new GetLightTemperatureNetworkThread(lightID, runWhenDone);
        lightRequest.start();
    }

    private class GetLightEnabledNetworkThread extends Thread {
        int lightID;
        RunWhenDoneState runWhenDone;
        GetLightEnabledNetworkThread(int lightID, RunWhenDoneState runWhenDone) {
            this.lightID = lightID;
            this.runWhenDone = runWhenDone;
        }

        @Override
        public void run() {
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder().url("http://" + device_ip + "/api/" + apikey + "/lights/" + Integer.toString(lightID)).get().build();
            try {
                Response response = connection.newCall(request).execute();
                JSONObject object = new JSONObject(response.body().string());
                runWhenDone.run(object.getJSONObject("state").getBoolean("on"));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public interface RunWhenDoneState {
        void run(boolean state);
    }

    private class SetLightNetworkThread extends Thread {
        int lightID;
        boolean state;
        SetLightNetworkThread(int lightID, boolean state) {
            this.lightID = lightID;
            this.state = state;
        }
        @Override
        public void run() {
            String bodyText = "{\"on\": "+ Boolean.toString(state) +"}";
            OkHttpClient connection = new OkHttpClient();
            RequestBody body = RequestBody.create(bodyText, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url("http://" + device_ip + "/api/" + apikey + "/lights/" + Integer.toString(lightID) + "/state/")
                                                    .put(body)
                                                    .build();
            try {
                Response response = connection.newCall(request).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class GetLightBrightnessNetworkThread extends Thread {
        int lightID;
        RunWhenDoneBrightness runWhenDone;
        GetLightBrightnessNetworkThread(int lightID, RunWhenDoneBrightness runWhenDone) {
            this.lightID = lightID;
            this.runWhenDone = runWhenDone;
        }

        @Override
        public void run() {
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder().url("http://" + device_ip + "/api/" + apikey + "/lights/" + Integer.toString(lightID)).get().build();
            try {
                Response response = connection.newCall(request).execute();
                JSONObject object = new JSONObject(response.body().string());
                runWhenDone.run(object.getJSONObject("state").getInt("bri"));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public interface RunWhenDoneBrightness {
        void run(int temperature);
    }

    private class SetLightBrightnessNetworkThread extends Thread {
        int lightID;
        int brightness;
        SetLightBrightnessNetworkThread(int lightID, int brightness) {
            this.lightID = lightID;
            this.brightness = brightness;
        }
        @Override
        public void run() {
            String bodyText = "{\"bri\": "+ Integer.toString(brightness) +"}";
            OkHttpClient connection = new OkHttpClient();
            RequestBody body = RequestBody.create(bodyText, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url("http://" + device_ip + "/api/" + apikey + "/lights/" + Integer.toString(lightID) + "/state/")
                    .put(body)
                    .build();
            try {
                Response response = connection.newCall(request).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class GetLightTemperatureNetworkThread extends Thread {
        int lightID;
        RunWhenDoneTemperature runWhenDone;
        GetLightTemperatureNetworkThread(int lightID, RunWhenDoneTemperature runWhenDone) {
            this.lightID = lightID;
            this.runWhenDone = runWhenDone;
        }

        @Override
        public void run() {
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder().url("http://" + device_ip + "/api/" + apikey + "/lights/" + Integer.toString(lightID)).get().build();
            try {
                Response response = connection.newCall(request).execute();
                JSONObject object = new JSONObject(response.body().string());
                runWhenDone.run(object.getJSONObject("state").getInt("ct"));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public interface RunWhenDoneTemperature {
        void run(int brightness);
    }

    private class SetLightTemperatureNetworkThread extends Thread {
        int lightID;
        int temperature;
        SetLightTemperatureNetworkThread(int lightID, int temperature) {
            this.lightID = lightID;
            this.temperature = temperature;
        }
        @Override
        public void run() {
            String bodyText = "{\"ct\": "+ Integer.toString(temperature) +"}";
            OkHttpClient connection = new OkHttpClient();
            RequestBody body = RequestBody.create(bodyText, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder().url("http://" + device_ip + "/api/" + apikey + "/lights/" + Integer.toString(lightID) + "/state/")
                    .put(body)
                    .build();
            try {
                Response response = connection.newCall(request).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static class BridgeDiscoveryThread extends Thread {
        public String device_ip = null;

        public BridgeDiscoveryThread() {

        }

        @Override
        public void run() {
            System.out.println("Searching for Device");
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder().url("https://discovery.meethue.com").get().build();
            try {
                Response response = connection.newCall(request).execute();
                JSONArray responseParsed = new JSONArray(response.body().string());
                device_ip = responseParsed.getJSONObject(0).getString("internalipaddress");
                System.out.println("Success Finding Device");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static class ApiKeyGenThread extends Thread {

        String device_ip;
        public String apiKey = null;

        public ApiKeyGenThread(String device_ip) {
            this.device_ip = device_ip;
        }

        @Override
        public void run() {
            RequestBody requestBody = RequestBody.create("{\"devicetype\":\"smartHomeControl#" + Integer.toString(new Random().nextInt()) + "\"}", MediaType.get("application/json; charset=utf-8"));
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder().url("http://" + device_ip + "/api/").post(requestBody).build();
            try {
                Response response = connection.newCall(request).execute();
                String bodyText = response.body().string();
                System.out.println(bodyText);
                JSONArray responseParsed = new JSONArray(bodyText);
                apiKey = responseParsed.getJSONObject(0).getJSONObject("success").getString("username");
            } catch (IOException | JSONException e) {
                e.printStackTrace();

            }
        }
    }

    public static class LightDiscoveryThread extends Thread {

        String device_ip;
        String apikey;
        public HashMap<String, String> lightNames = new HashMap<>();
        public HashMap<String, String> lightTypes = new HashMap<>();

        public LightDiscoveryThread(String device_ip, String apikey) { this.device_ip = device_ip; this.apikey = apikey; }

        @Override
        public void run() {
            OkHttpClient connection = new OkHttpClient();
            Request request = new Request.Builder().url("http://" + device_ip + "/api/" + apikey + "/lights").get().build();
            try {
                Response response = connection.newCall(request).execute();
                String bodyText = response.body().string();
                System.out.println(bodyText);
                JSONObject responseParsed = new JSONObject(bodyText);
                Iterator<String> keys = responseParsed.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    lightNames.put(key, responseParsed.getJSONObject(key).getString("name"));
                    lightTypes.put(key, responseParsed.getJSONObject(key).getString("type"));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
