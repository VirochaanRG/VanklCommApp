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

    // Constructor
    public NetworkTask() {
        // Default constructor
    }

    // Method to perform network operations in the background
    @Override
    protected String doInBackground(String... urls) {
        // Initialize an empty response string
        String response = "";
        // Iterate through the provided URLs
        for (String url : urls) {
            // Make an HTTP request and append the response to the response string
            response += makeHttpRequest(url);
        }
        // Return the combined response from all URLs
        return response;
    }

    // Method to make an HTTP request to the specified URL
    private String makeHttpRequest(String urlString) {
        // Initialize a string builder to store the response
        StringBuilder response = new StringBuilder();
        try {
            // Create a URL object from the provided URL string
            URL url = new URL(urlString);
            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Get the input stream from the connection
            InputStream inputStream = connection.getInputStream();
            // Create a buffered reader to read the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // Read each line of the response and append it to the string builder
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            // Close the reader and disconnect the connection
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            // Print stack trace if an IOException occurs
            e.printStackTrace();
        }
        // Return the response as a string
        return response.toString();
    }

    // Method to handle the result after the background task is completed
    @Override
    protected void onPostExecute(String result) {
        // Print the response to the console
        System.out.println("Response: " + result);
    }
}
