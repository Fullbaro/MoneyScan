package com.example.moneyscan;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class versionCheck extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        try{
            URL url = new URL(urls[0]);
            HttpURLConnection conn =(HttpURLConnection) url.openConnection();
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "null";
            while((line = reader.readLine()) != null){
                result += line;
            }
            conn.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
}

