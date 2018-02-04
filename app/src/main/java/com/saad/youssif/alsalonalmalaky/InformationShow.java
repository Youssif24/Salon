package com.saad.youssif.alsalonalmalaky;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InformationShow extends Activity {

    TextView info_username,info_phone,info_reply;
    LinearLayout info_layout,reply_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_show);
        info_username=(TextView)findViewById(R.id.info_usernameTv);
        info_phone=(TextView)findViewById(R.id.info_phoneTv);
        info_layout=(LinearLayout)findViewById(R.id.info_layout);
        reply_layout=(LinearLayout)findViewById(R.id.reply_layout);
        info_reply=(TextView)findViewById(R.id.info_replyTv);
        getInfo();

    }


    public void getInfo()
    {
        Intent intent=getIntent();
        String option=intent.getExtras().getString("option");
        if(option.equals("info"))
        {
            setTitle("معلومات العميل");
            reply_layout.setVisibility(View.GONE);
            info_username.setText(info_username.getText().toString()+intent.getExtras().getString("info_name"));
            info_phone.setText(info_phone.getText().toString()+intent.getExtras().get("info_phone"));
        }
        else
        {
            setTitle("الرد");
            info_layout.setVisibility(View.GONE);
            info_reply.setText(intent.getExtras().getString("info_reply"));
        }
    }
}
