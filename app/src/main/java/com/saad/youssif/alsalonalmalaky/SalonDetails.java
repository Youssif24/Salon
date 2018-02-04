package com.saad.youssif.alsalonalmalaky;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SalonDetails extends AppCompatActivity {

    TextView showMapTv,uberTv;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_details);
        showMapTv=(TextView) findViewById(R.id.showMapTextView);
        uberTv=(TextView)findViewById(R.id.uberTextView);
        showMapTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalonDetails.this,MapsActivity.class));
            }
        });

        uberTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapsIntent=new Intent(Intent.ACTION_VIEW);
                mapsIntent.setData(Uri.parse("geo:31.4429141,31.6814164?z=17"));
                Intent chooser = Intent.createChooser(mapsIntent,"توصيل عن طريق : ");
                if(mapsIntent.resolveActivity(getPackageManager())!=null)
                {
                    startActivity(chooser);
                }
            }
        });

    }
}
