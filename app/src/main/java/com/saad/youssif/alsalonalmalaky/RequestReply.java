package com.saad.youssif.alsalonalmalaky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RequestReply extends Activity {
    ProgressDialog progressDialog;
    SharedPreferences shrd;
    //ConnectivityManager connectivityManager;
    EditText replyEditText;
    TextView sendTv,cancelTv;
    String res_id,res_date,request_user_id;

    public static final String SEND_REQUEST_REPLY="https://youssifsaad96.000webhostapp.com/upload_reply.php";
    public static final String SEND_PUSH_NOTIFICATION="https://youssifsaad96.000webhostapp.com/sendSinglePush.php";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reply);
        shrd = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        progressDialog=new ProgressDialog(this);
        replyEditText=(EditText)findViewById(R.id.replyEditText);
        sendTv=(TextView)findViewById(R.id.sendReplyTv);
        cancelTv=(TextView)findViewById(R.id.cancel_replyTv);
        res_id=getIntent().getExtras().getString("req_id");
        request_user_id=getIntent().getExtras().getString("req_user_id");
        setTitleColor(R.color.main);

        // (1) get today's date
        Date today = Calendar.getInstance().getTime();
        int hour=today.getHours();
        int minute=today.getMinutes();

        // (2) create a date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm");


        // (3) create a new String using the date format we want
        res_date = formatter.format(today);



        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestReply.this.finish();
            }
        });

        sendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=shrd.edit();
                String switchState=shrd.getString("switchState",null);
               if(validateReply())
               {
                   uploadReply();
                   if(switchState.equals("true"))
                   {
                       sendPushNotification();
                   }
               }
               else
               {
                   replyEditText.setError("من فضلك اكتب الرد");
               }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RequestReply.this,MainActivity.class));

    }

    private boolean validateReply()
    {
        if(replyEditText.equals(""))
        {
            return false;
        }
        else
        {
            return true;
        }


    }


    public void sendPushNotification()
    {
        SharedPreferences.Editor editor=shrd.edit();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, SEND_PUSH_NOTIFICATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               // Toast.makeText(RequestReply.this,response,Toast.LENGTH_LONG).show();
            }


        }


                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RequestReply.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mapData = new HashMap<>();
                mapData = new HashMap<>();
                mapData.put("user_id",request_user_id);
                mapData.put("title", "تم رد الادمن علي طلبك");
                mapData.put("message", replyEditText.getText().toString().trim());


                return mapData;

            }
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
        RequestQueue requestQueue = Volley.newRequestQueue(RequestReply.this);
        requestQueue.add(stringRequest);
    }

    public void uploadReply()
    {
            progressDialog.setMessage("من فضلك انتظر...");
            progressDialog.show();
            StringRequest stringRequest=new StringRequest(com.android.volley.Request.Method.POST, SEND_REQUEST_REPLY, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        progressDialog.dismiss();
                        Toast.makeText(RequestReply.this, "تم ارسال الرد بنجاح", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(RequestReply.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    }
                    else if(response.equals("exists"))
                    {
                        replyEditText.setError("تم الرد علي هذا الطلب");
                        progressDialog.dismiss();
                        return;

                    }
                    else
                        {
                        Toast.makeText(RequestReply.this, "من فضلك حاول مره اخره !!!!", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }


            }



                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RequestReply.this,error.toString(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            }

            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> mapData=new HashMap<>();
                    mapData=new HashMap<>();
                    mapData.put("response_date",res_date);
                    mapData.put("response",replyEditText.getText().toString());
                    mapData.put("request_id",res_id);
                    return mapData;

                }
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
            RequestQueue requestQueue= Volley.newRequestQueue(RequestReply.this);
            requestQueue.add(stringRequest);

        }

}
