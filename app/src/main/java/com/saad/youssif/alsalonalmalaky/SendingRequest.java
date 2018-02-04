package com.saad.youssif.alsalonalmalaky;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SendingRequest extends AppCompatActivity {

    TextView dayPickerTv,timePickerTV;
    EditText descEditText;
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private Calendar calendar;
    String rdate="",rtime="";
    TextView sendRequestTv;
    ProgressDialog progressDialog;
    SharedPreferences shrd;
    ConnectivityManager connectivityManager;
    String  end_date_string,currentDate;




    public static final String SEND_upload_request_DATA_URL="https://youssifsaad96.000webhostapp.com/upload_request.php";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending_request);
        dayPickerTv=(TextView)findViewById(R.id.dayPickerTv);
        //timePickerTV=(TextView)findViewById(R.id.timePickerTv);
        sendRequestTv =(TextView) findViewById(R.id.sendRequestBtn);
        descEditText=(EditText)findViewById(R.id.descEditText);
        progressDialog = new ProgressDialog(this);
        shrd=getSharedPreferences("UserLogin",this.MODE_PRIVATE);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Date today = Calendar.getInstance().getTime();
        String hour=String.valueOf(today.getHours());
        String minute=String.valueOf(today.getMinutes());
        rtime=hour+minute;



        dayPickerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(SendingRequest.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,datePickerDialog,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        datePickerDialog=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                int end_date=dayOfMonth+2;
                rdate=year+"-"+month+"-"+dayOfMonth;
                end_date_string=year+"-"+month+"-"+end_date;

                dayPickerTv.setText(rdate);

            }
        };

       /* timePickerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int hour=calendar.get(Calendar.HOUR);
                int minute=calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog=new TimePickerDialog(SendingRequest.this,timeSetListener,hour,minute,false);
                //timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();

            }
        });*/
        /*timeSetListener= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                rtime=hourOfDay+":"+minute;
                String am_pm = (hourOfDay < 12) ? "AM" : "PM";
                //timePickerTV.setText("الوقت : "+rtime+am_pm);
            }
        };*/


        sendRequestTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection())
                {
                    uploadRequest();


                }
                else
                {
                    Toast.makeText(SendingRequest.this,"عفوا لا يوجد اتصال انترنت !!!",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    /*public void sendPushNotification()
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
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SendingRequest.this,MainActivity.class));
        SendingRequest.this.finish();
    }

    public boolean checkConnection()
    {
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null&&info.isConnected();
    }

    public void uploadRequest()
    {
        if(rdate.equals("")||rtime.equals(""))
        {
            Toast.makeText(SendingRequest.this,"من فضلك اختر اليوم....",Toast.LENGTH_LONG).show();
        }
        else
        {
            progressDialog.setMessage("من فضلك انتظر...");
            progressDialog.show();
            StringRequest stringRequest=new StringRequest(Request.Method.POST, SEND_upload_request_DATA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(SendingRequest.this,"date : "+rdate+" time : "+rtime+"end_date"+end_date_string,Toast.LENGTH_LONG).show();
                    if(response.equals("success"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SendingRequest.this,"تم ارسال الطلب بنجاح",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(SendingRequest.this,MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();



                    }
                    else
                    {
                        Toast.makeText(SendingRequest.this,"من فضلك حاول مره اخره !!!!",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }


            }



                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SendingRequest.this,error.toString(),Toast.LENGTH_LONG).show();
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
                    mapData.put("rdate",rdate);
                    mapData.put("desc",descEditText.getText().toString());
                    mapData.put("user_id",user_id);
                    mapData.put("end_date", end_date_string);
                    mapData.put("rtime", rtime);
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
            RequestQueue requestQueue= Volley.newRequestQueue(SendingRequest.this);
            requestQueue.add(stringRequest);

        }
    }





}
