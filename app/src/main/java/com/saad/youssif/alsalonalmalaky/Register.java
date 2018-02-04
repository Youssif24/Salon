package com.saad.youssif.alsalonalmalaky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Register extends AppCompatActivity {
    private EditText nameEt, phoneEt, passEt;
    private TextView registerTextView;
    private ProgressDialog progressDialog;
    private ConnectivityManager connectivityManager;
    public static final String SEND_register_DATA_URL="https://youssifsaad96.000webhostapp.com/register.php";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //setTitle("تسجيل حساب ");
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        progressDialog = new ProgressDialog(this);
        nameEt = (EditText) findViewById(R.id.registerNameEt);
        passEt = (EditText) findViewById(R.id.registerPasswordEt);
        phoneEt = (EditText) findViewById(R.id.registerphoneEt);
        registerTextView = (TextView) findViewById(R.id.regisertTextView);
        progressDialog.setCanceledOnTouchOutside(false);


        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateName()&&validatePassword())
                {
                    if(checkConnection()){
                        progressDialog.setMessage("تسجيل حساب ......");
                        progressDialog.show();
                        insertUser();}
                    else
                        Toast.makeText(Register.this,"عفوا لا يوجد اتصال انترنت",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void custonmFont()
    {
        Typeface myCustomFont=Typeface.createFromAsset(getAssets(),"fonts/myriad_arabic_regular.otf");
        nameEt.setTypeface(myCustomFont);
        phoneEt.setTypeface(myCustomFont);
        passEt.setTypeface(myCustomFont);
        registerTextView.setTypeface(myCustomFont);

    }
    public boolean checkConnection()
    {
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null&&info.isConnected();
    }

   public void insertUser()
    {
        final String Name=nameEt.getText().toString().trim();
        final String Phone=phoneEt.getText().toString().trim();
        final String Password=passEt.getText().toString().trim();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SEND_register_DATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("success"))
                {
                    progressDialog.dismiss();
                    Toast.makeText(Register.this,"تم التسجيل بنجاح",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Register.this, Login.class));
                    Register.this.finish();

                }
                else if(response.equals("exists"))
                {
                    progressDialog.dismiss();
                    nameEt.setError("إسم العميل موجود");
                }
                else
                    {
                        Toast.makeText(Register.this,"من فضلك حاول مره اخره",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                }


            }



                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ( error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Communication Error!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                }
                else if(error instanceof TimeoutError)
                {
                    Toast.makeText(getApplicationContext(), "time out Error!", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                else if (error instanceof AuthFailureError) {
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

        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> mapData=new HashMap<>();
                mapData=new HashMap<>();
                mapData.put("username",Name);
                mapData.put("password",Password);
                mapData.put("phone",Phone);
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
        RequestQueue requestQueue= Volley.newRequestQueue(Register.this);
        requestQueue.add(stringRequest);


    }





    public boolean validateName()
    {
        if(nameEt.getText().toString().equals(""))
        {
            nameEt.setError("ادخل الاسم");
            return false;
        }

        return true;
    }

    public boolean validatePassword()
    {
        if(passEt.getText().toString().equals("") )
        {
            passEt.setError("ادخل كلمة المرور");
            return false;
        }
        else
        {
            return true;
        }

    }


    }





