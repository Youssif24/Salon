package com.saad.youssif.alsalonalmalaky;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuggestionActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ConnectivityManager connectivityManager;
    private ProgressBar progressBar;
    SuggestionRecyclerAdapter mAdapter;
    public List<Suggestion> suggestionList;
    ProgressDialog progressDialog;
    public final String GET_SUGGESTIONS_DATA = "https://youssifsaad96.000webhostapp.com/get_suggestions.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerRequests);
        progressDialog = new ProgressDialog(this);

        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SuggestionActivity.this,UploadingSuggestion.class));
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        if (checkConnection()) {
            progressDialog.setMessage("جاري تحميل الإقتراحات.......");
            progressDialog.show();
            getSuggestions();
        } else {
            Toast.makeText(SuggestionActivity.this, "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
        }
    }

    public boolean checkConnection() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public void getSuggestions()
    {
        suggestionList = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, GET_SUGGESTIONS_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    Suggestion suggestion;
                    //Toast.makeText(SuggestionActivity.this,response,Toast.LENGTH_LONG).show();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        if (!(jsonArray.isNull(0))) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject user_object = jsonArray.getJSONObject(i);
                                suggestion = new Suggestion();
                                suggestion.setDetails(user_object.getString("sug_details"));
                                suggestion.setTime(user_object.getString("sug_time"));
                                suggestion.setUsername(user_object.getString("username"));

                                suggestionList.add(suggestion);
                            }
                            mAdapter = new SuggestionRecyclerAdapter(suggestionList, SuggestionActivity.this);
                            recyclerView.setAdapter(mAdapter);
                            progressDialog.dismiss();

                        } else {

                            Toast.makeText(SuggestionActivity.this, " لا يوجد إقتراحات حاليا", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SuggestionActivity.this, "اتصال الانترنت ضعيف!!!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }

        ) {

        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            public int getCurrentTimeout() {
                return 5000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 1000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(SuggestionActivity.this);
        requestQueue.add(stringRequest);

    }



}
