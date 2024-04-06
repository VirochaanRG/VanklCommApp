package com.example.vanklcommapp.KDC.NetworkAcess;

public class AuthenticationNetworkTask extends NetworkTask {


    @Override
    protected void onPostExecute(String result) {
        System.out.println("Authentication Result: " + result);
    }

}
