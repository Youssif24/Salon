package com.saad.youssif.alsalonalmalaky;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Login extends AppCompatActivity {
    private ImageView logoImgView;
    private EditText usernameET, passwordEt;
    Button loginBtn;
    private TextView rgstTv, loginTv;
    Animation animUp, animDown;
    ProgressDialog progressDialog;
    ConnectivityManager connectivityManager;
    SharedPreferences shrd;
    public static final String SEND_login_DATA_URL = "https://youssifsaad96.000webhostapp.com/user_login.php";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        shrd=getSharedPreferences("UserLogin",this.MODE_PRIVATE);
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        logoImgView=(ImageView)findViewById(R.id.imageView);
        usernameET =(EditText)findViewById(R.id.editText);
        passwordEt=(EditText)findViewById(R.id.editText2);
        rgstTv=(TextView)findViewById(R.id.textView2);
        loginTv=(TextView) findViewById(R.id.textView);

        //animViews();
        register_from_shrd();


        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection())
                {
                    user_login();
                }

                else
                    Toast.makeText(Login.this,"لا يوجد اتصال انترنت !!!",Toast.LENGTH_LONG).show();
            }
        });

        rgstTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rgstIntent=new Intent(Login.this,Register.class);
                startActivity(rgstIntent);
            }
        });
    }

    public boolean checkConnection()
    {
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null&&info.isConnected();
    }


    public void save_user_data(String username,String id,String type)
    {
        SharedPreferences.Editor editor=shrd.edit();
        editor.putString("id",id.trim()).commit();
        editor.putString("username",username.trim()).commit();
        editor.putString("type",type.trim()).commit();
        editor.putString("password",passwordEt.getText().toString().trim()).commit();
        editor.putString("switchState","true").commit();
    }
    public void register_from_shrd()
    {
        SharedPreferences.Editor editor=shrd.edit();
        String email=shrd.getString("username","");
        String pass=shrd.getString("password","");
        if(!(TextUtils.isEmpty(email))&&!(TextUtils.isEmpty(pass)))
        {
            startActivity(new Intent(Login.this,MainActivity.class));
            Login.this.finish();
        }
    }

    public void user_login()
    {
        final String login_username=usernameET.getText().toString().trim();
        final String login_password=passwordEt.getText().toString().trim();


        if(TextUtils.isEmpty(login_username))
        {
            Toast.makeText(Login.this,"من فضلك ادخل أسم العميل .....",Toast.LENGTH_LONG).show();
            return;

        }
        else if(TextUtils.isEmpty(login_password))
        {
            Toast.makeText(Login.this,"من فضلك ادخل كلمة المرور",Toast.LENGTH_LONG).show();
            return;
        }
        else
        {
            progressDialog.setMessage("من فضلك انتظر....");
            progressDialog.show();
            StringRequest stringRequest=new StringRequest(Request.Method.POST, SEND_login_DATA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response!=null)
                    {

                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            if(!(jsonArray.isNull(0)))
                            {
                                JSONObject user_object=jsonArray.getJSONObject(0);
                                String id=user_object.getString("id");
                                String username=user_object.getString("username");
                                String type=user_object.getString("type");
                                Intent user_intent=new Intent(Login.this,MainActivity.class);
                                progressDialog.dismiss();
                                Toast.makeText(Login.this,"تم الدخول بنجاح",Toast.LENGTH_LONG).show();
                                save_user_data(username,id,type);
                                startActivity(user_intent);
                                Login.this.finish();
                            }
                            else
                            {

                                Toast.makeText(Login.this," من فضلك حاول مره اخره",Toast.LENGTH_LONG).show();
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
                    mapData.put("login_username",login_username);
                    mapData.put("login_password",login_password);
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
            RequestQueue requestQueue= Volley.newRequestQueue(Login.this);
            requestQueue.add(stringRequest);


        }

        }

        public void custonmFont()
        {
            Typeface myCustomFont=Typeface.createFromAsset(getAssets(),"fonts/myriad_arabic_regular.otf");
            loginTv.setTypeface(myCustomFont);
            rgstTv.setTypeface(myCustomFont);
            usernameET.setTypeface(myCustomFont);
            passwordEt.setTypeface(myCustomFont);
        }


    }

