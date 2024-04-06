package com.example.vanklcommapp.KDC.NetworkAcess;

import android.os.AsyncTask;

import com.example.vanklcommapp.Models.AccountModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public abstract class NetworkTask extends AsyncTask<String, Void, String> {

    public NetworkTask(){

    }
    @Override
    protected String doInBackground(String... urls) {
        String response = "";
        for (String url : urls) {
            response += makeHttpRequest(url);
        }
        return response;
    }

    private String makeHttpRequest(String urlString) {
        System.out.println("We are in here");
        System.out.println(urlString);
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("Response: " + result);
    }

}
