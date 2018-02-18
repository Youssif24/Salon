package com.saad.youssif.alsalonalmalaky;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity
{
    SharedPreferences shrd;
    public List<Request> requestsList;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private ConnectivityManager connectivityManager;
    private ProgressBar progressBar;
    RecyclerAdapter mAdapter;
    public static String type;
    ProgressDialog progressDialog;
    BroadcastReceiver broadcastReceiver;
    ImageView myMsgImgView,detailsImg,settingsImageView,sendImg;

    public final String GET_REQUEST_DATA = "https://youssifsaad96.000webhostapp.com/get_requests.php";
    public final String DELETE_REQUEST_DATA = "https://youssifsaad96.000webhostapp.com/delete_request.php";
    public final String DEVICE_TOKEN_DATA = "https://youssifsaad96.000webhostapp.com/RegisterDevice.php";



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("جميع الطلبات : ");
        myMsgImgView=(ImageView)findViewById(R.id.myMsgImg);
        detailsImg=(ImageView)findViewById(R.id.profileImg);
        settingsImageView=(ImageView)findViewById(R.id.settingImg);
        sendImg=(ImageView)findViewById(R.id.sendImg);
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        shrd = getSharedPreferences("UserLogin", this.MODE_PRIVATE);
        determineUserType();
        /*if(type.equals("admin"))
        {
            sendImgView.setVisibility(View.GONE);
        }*/



        recyclerView = (RecyclerView) findViewById(R.id.recyclerRequests);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(this);

        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter(MyFirebaseInstanceIDService.TOKEN_BROADCAST));
        if(!checkTokenStatus())
        {
            sendTokenToServer();
        }

        if (checkConnection()) {
            new NetworkTask().execute();
        } else {
            Toast.makeText(MainActivity.this, "لا يوجد اتصال بالانترنت", Toast.LENGTH_LONG).show();
        }



       myMsgImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                MainActivity.this.finish();
            }
        });


        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SendingRequest.class));
            }
        });

        detailsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SalonDetails.class));
            }
        });


        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,UserProfile.class));
            }
        });







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.share_location_option:
                Intent mapsIntent=new Intent(Intent.ACTION_VIEW);
                mapsIntent.setData(Uri.parse("geo:31.4429141,31.6814164?z=17"));
                Intent chooser = Intent.createChooser(mapsIntent,"مشاركة عنوان الصالون إلي : ");
                if(mapsIntent.resolveActivity(getPackageManager())!=null)
                {
                    startActivity(chooser);
                }

                return true;
            case R.id.logout_option:
                SharedPreferences.Editor editor = shrd.edit();
                editor.clear().commit();
                startActivity(new Intent(MainActivity.this, Login.class));
                MainActivity.this.finish();
                return true;
            case R.id.suggest_option:
                startActivity(new Intent(MainActivity.this,SuggestionActivity.class));
                return true;
            case R.id.salon_details_option:
                startActivity(new Intent(MainActivity.this,SalonDetails.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean checkTokenStatus()
    {
        SharedPreferences.Editor editor=shrd.edit();
        String status=shrd.getString("status","no");
        if(status.equals("done"))
        {
            return true;
        }
        else
        {
            return  false;
        }
    }


    public void sendTokenToServer()
    {
        final SharedPreferences.Editor editor=shrd.edit();
        final String id=shrd.getString("id",null);
        final String token=SharedPrefManager.getInstance(MainActivity.this).getDeviceToken();
        if(TextUtils.isEmpty(token))
        {
            Toast.makeText(MainActivity.this,"Token is not generated",Toast.LENGTH_LONG).show();
        }
        else
        {

            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, DEVICE_TOKEN_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.equals("success")) {
                        editor.putString("status","done").commit();

                    }
                    else
                    {
                        editor.putString("status","no").commit();
                    }

                }


            }


                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();

                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> mapData = new HashMap<>();
                    mapData = new HashMap<>();
                    mapData.put("user_id", id);
                    mapData.put("token",token);

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
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);



        }




    }



    public boolean checkConnection() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public class NetworkTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            requestsList = new ArrayList<>();
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, GET_REQUEST_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        Request request;
                        //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            if (!(jsonArray.isNull(0))) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject user_object = jsonArray.getJSONObject(i);
                                    request = new Request();
                                    request.setId(user_object.getString("req_id"));
                                    request.setDate(user_object.getString("rdate"));
                                    request.setDesc(user_object.getString("desc"));
                                    request.setTime(user_object.getString("rtime"));
                                    request.setUsername(user_object.getString("username"));
                                    request.setResponse(user_object.getString("response"));
                                    request.setUser_id(user_object.getString("user_id"));
                                    request.setPhone(user_object.getString("phone"));


                                    requestsList.add(request);
                                }
                                mAdapter = new RecyclerAdapter(requestsList, MainActivity.this);
                                recyclerView.setAdapter(mAdapter);

                            } else {

                                Toast.makeText(MainActivity.this, " لا يوجد طلبات حاليا", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "اتصال الانترنت ضعيف!!!", Toast.LENGTH_LONG).show();
                    // progressDialog.dismiss();

                }
            }

            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> mapData = new HashMap<>();
                    mapData = new HashMap<>();
                    SharedPreferences.Editor editor = shrd.edit();
                    String user_id = shrd.getString("id", "0");
                    mapData.put("user_id", user_id);
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
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(stringRequest);
            return null;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

        }
    }


    public void determineUserType() {
        SharedPreferences.Editor editor = shrd.edit();
        type = shrd.getString("type", "0");
        if (type.equals("user")) {
            setTitle("الطلبات المرسله : ");
        }
        else
        {
            sendImg.setVisibility(View.GONE);
        }
    }


    public void delete_request(final String req_id, final Context context) {
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, DELETE_REQUEST_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Success")) {
                    Toast.makeText(context, "تم حذف الطلب بنجاح ", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context, "من فضلك حاول مره اخره !!!!", Toast.LENGTH_LONG).show();
                }

            }


        }

                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> mapData = new HashMap<>();
                mapData = new HashMap<>();
                mapData.put("request_id", req_id);

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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

}

