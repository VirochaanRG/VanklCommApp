package com.example.vanklcommapp.KDC.NetworkAcess;

//Implementation of the Network Task for Authentication
public class AuthenticationNetworkTask extends NetworkTask {
    @Override
    protected void onPostExecute(String result) {
        System.out.println("Authentication Result: " + result);
    }

}
