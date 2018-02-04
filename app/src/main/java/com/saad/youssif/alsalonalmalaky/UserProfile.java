package com.saad.youssif.alsalonalmalaky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserProfile extends AppCompatActivity {

    TextView profile_usernameTv,profile_phoneTv,profile_typeTv,changePassTv;
    ProgressDialog progressDialog;
    ConnectivityManager connectivityManager;
    SharedPreferences shrd;
    Switch mSwitch;
    public static final String USER_PROFILE_DATA_URL = "https://youssifsaad96.000webhostapp.com/get_profile.php";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profile_usernameTv=(TextView)findViewById(R.id.profile_usernameTv);
        profile_phoneTv=(TextView)findViewById(R.id.profile_phoneTv);
        profile_typeTv=(TextView)findViewById(R.id.profile_typeTv);
        changePassTv=(TextView)findViewById(R.id.profile_changePassTv);
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        shrd=getSharedPreferences("UserLogin",this.MODE_PRIVATE);
        mSwitch=(Switch) findViewById(R.id.switch1);
        getSwitchState();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("جاري تحميل البيانات....");
        progressDialog.show();
        if(checkConnection())
        {
            get_user_profile();
        }
        else
        {
            Toast.makeText(UserProfile.this,"لا يوجد اتصال انترنت !!!",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        changePassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this,UpdatePassword.class));
            }
        });

    }


    public void getSwitchState()
    {
        boolean switchState=mSwitch.isChecked();
        SharedPreferences.Editor editor=shrd.edit();
        if(switchState==true)
        {
            editor.putString("switchState","true").commit();
        }
        else
        {
            editor.putString("switchState","false").commit();
        }

    }




    public boolean checkConnection()
    {
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null&&info.isConnected();
    }
    public void get_user_profile() {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, USER_PROFILE_DATA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("user_result");
                            if (!(jsonArray.isNull(0))) {
                                JSONObject user_object = jsonArray.getJSONObject(0);
                                profile_usernameTv.setText(user_object.getString("username"));
                                profile_phoneTv.setText(profile_phoneTv.getText().toString()+user_object.getString("phone"));
                                profile_typeTv.setText(profile_typeTv.getText().toString()+user_object.getString("type"));
                                progressDialog.dismiss();

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }


                }


            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(), "Communication Error!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(getApplicationContext(), "time out Error!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else if (error instanceof ServerError) {
                        Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }


            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> mapData = new HashMap<>();
                    mapData = new HashMap<>();
                    SharedPreferences.Editor editor=shrd.edit();
                    String id=shrd.getString("id","0");
                    mapData.put("user_id",id);
                    return mapData;

                }
            };
            stringRequest.setRetryPolicy(new RetryPolicy() {
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 10000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(UserProfile.this);
            requestQueue.add(stringRequest);


    }
}
