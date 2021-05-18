package com.example.moneyscan;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Database {

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    // Storing server url into String variable.
    String HttpUrl;

    Context context;

    public Database(Context context){
        this.context = context;
    }


    public void send(String adat) {
        HttpUrl = "https://balintdaniel.com/moneyupload/insert_record.php";

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(context);

        progressDialog = new ProgressDialog(context);

        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Kérlek várj, küldöm az adatokat a szervernek.");
        progressDialog.show();


        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing response message coming from server.
                        Toast.makeText(context, ServerResponse, Toast.LENGTH_LONG).show();
                        Log.e("Adatbázis", ServerResponse);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing error message if something goes wrong.
                        Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Adatbázis", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("kod", adat.split("-")[0]);
                params.put("ev", adat.split("-")[1]);

                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    public void delete(String adat){
        HttpUrl = "https://balintdaniel.com/moneyupload/delete_record.php";
        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(context);

        progressDialog = new ProgressDialog(context);

        // Showing progress dialog at user registration time.
        progressDialog.setMessage("A törlés folyamatban.");
        progressDialog.show();


        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing response message coming from server.
                        Toast.makeText(context, ServerResponse, Toast.LENGTH_LONG).show();
                        Log.e("Adatbázis", ServerResponse);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
                        progressDialog.dismiss();

                        // Showing error message if something goes wrong.
                        Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                        Log.e("Adatbázis", volleyError.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("kod", adat.split("-")[0]);
                params.put("ev", adat.split("-")[1]);

                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

}