package com.zjucompass.zjucampus3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class AboutActivity extends ActionBarActivity {

    private int[] imgs = {R.drawable.about_img, R.drawable.zyy, R.drawable.lsl, R.drawable.wzf};
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final ImageView aboutImage = (ImageView) findViewById(R.id.about_img);
        aboutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if(index >= 4){
                    index = 0;
                }
                aboutImage.setImageResource(imgs[index]);
            }
        });

        TextView contactView = (TextView) findViewById(R.id.about_contact);
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@zjucompass.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT   , "");
                startActivity(Intent.createChooser(i, "发送邮件..."));
            }
        });
    }

}
