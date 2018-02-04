package com.saad.youssif.alsalonalmalaky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UpdatePassword extends Activity {


    TextView newPassEditText,retypePassEditText;
    TextView changePassTv,cancelTv;
    ProgressDialog progressDialog;
    SharedPreferences shrd;
    ConnectivityManager connectivityManager;


    public static final String UPDATE_USER_PASSWORD_URL = "https://youssifsaad96.000webhostapp.com/update_user_password.php";



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        newPassEditText=(EditText)findViewById(R.id.newPassword);
        retypePassEditText=(EditText)findViewById(R.id.retypeNewPassword);
        changePassTv=(TextView)findViewById(R.id.changePassTv);
        cancelTv=(TextView)findViewById(R.id.cancelTv);
        shrd=getSharedPreferences("UserLogin",this.MODE_PRIVATE);
        connectivityManager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        progressDialog=new ProgressDialog(this);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePassword.this.finish();
            }
        });
        changePassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateNewPassword()&&checkConnection())
                {
                    updatePassword();
                }
            }
        });


    }
    public boolean validateNewPassword()
    {
        if(newPassEditText.getText().toString().trim().equals(""))
         {
        newPassEditText.setError("ادخل كلمة المرور الجديده");
        return false;
         }

        else if(retypePassEditText.getText().toString().trim().equals(""))
        {
            retypePassEditText.setError("اعد كتابة كلمة المرور الجديده");
            return false;
        }
        else if(!(newPassEditText.getText().toString().trim().equals(retypePassEditText.getText().toString().trim())))
        {
            retypePassEditText.setError("كلمة المرور غير متطابقه!!");
            return false;
        }
        else
        {
            return true;
        }


    }
    public boolean checkConnection()
    {
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        return info!=null&&info.isConnected();
    }


    public void updatePassword()
    {
        progressDialog.setMessage("جاري التحديث.....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, UPDATE_USER_PASSWORD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Success")) {
                    progressDialog.dismiss();
                    Toast.makeText(UpdatePassword.this,"تم تغيير كلمة المرور",Toast.LENGTH_LONG).show();
                    UpdatePassword.this.finish();
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(UpdatePassword.this,"من فضلك حاول مره اخري",Toast.LENGTH_LONG).show();
                }


            }


        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }


        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mapData = new HashMap<>();
                mapData = new HashMap<>();
                SharedPreferences.Editor editor=shrd.edit();
                String user_id=shrd.getString("id","0");
                mapData.put("user_id",user_id);
                mapData.put("password",newPassEditText.getText().toString().trim());
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
        RequestQueue requestQueue = Volley.newRequestQueue(UpdatePassword.this);
        requestQueue.add(stringRequest);

    }
}
