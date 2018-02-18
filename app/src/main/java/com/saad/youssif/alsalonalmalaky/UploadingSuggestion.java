package com.saad.youssif.alsalonalmalaky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UploadingSuggestion extends AppCompatActivity {

    EditText sug_detailsEditText;
    TextView send_suggestionBtn;
    private Calendar calendar;
    ProgressDialog progressDialog;
    SharedPreferences shrd;
    ConnectivityManager connectivityManager;
    String sug_time;
    public static final String SEND_upload_Suggestion_DATA_URL="https://youssifsaad96.000webhostapp.com/upload_suggestion.php";



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_suggestion);
        sug_detailsEditText=(EditText)findViewById(R.id.sug_descEditText);
        send_suggestionBtn=(TextView) findViewById(R.id.sendSuggestionBtn);
        progressDialog = new ProgressDialog(this);
        shrd=getSharedPreferences("UserLogin",this.MODE_PRIVATE);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Date today = Calendar.getInstance().getTime();
        String hour=String.valueOf(today.getHours());
        String minute=String.valueOf(today.getMinutes());
        sug_time=hour+":"+minute;

        send_suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection())
                {
                    upload_suggestion();
                }
                else
                {
                    Toast.makeText(UploadingSuggestion.this,"عفوا لا يوجد اتصال انترنت !!!",Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    public boolean checkConnection()
    {
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null&&info.isConnected();
    }

    public void upload_suggestion()
    {
        if(sug_detailsEditText.getText().toString().equals(""))
        {
            Toast.makeText(UploadingSuggestion.this,"من فضلك اكتب اقتراحك",Toast.LENGTH_LONG).show();
        }
        else
        {
            progressDialog.setMessage("من فضلك انتظر...");
            progressDialog.show();
            StringRequest stringRequest=new StringRequest(com.android.volley.Request.Method.POST, SEND_upload_Suggestion_DATA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(SendingRequest.this,"date : "+rdate+" time : "+rtime+"end_date"+end_date_string,Toast.LENGTH_LONG).show();
                    if(response.equals("success"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(UploadingSuggestion.this,"تم ارسال إقتراحك بنجاح",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(UploadingSuggestion.this,SuggestionActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();



                    }
                    else
                    {
                        Toast.makeText(UploadingSuggestion.this,"من فضلك حاول مره اخره !!!!",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }


            }



                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(UploadingSuggestion.this,error.toString(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
            }

            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences.Editor editor=shrd.edit();
                    String user_id=shrd.getString("id","");
                    Map<String,String> mapData=new HashMap<>();
                    mapData=new HashMap<>();
                    mapData.put("sug_desc",sug_detailsEditText.getText().toString());
                    mapData.put("sug_user_id",user_id);
                    mapData.put("sug_time", sug_time);
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
            RequestQueue requestQueue= Volley.newRequestQueue(UploadingSuggestion.this);
            requestQueue.add(stringRequest);

        }
    }
}
