package com.zjucompass.zjucampus3;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class FaqActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView answer4 = (TextView) findViewById(R.id.answer4);
        answer4.setText(Html.fromHtml("欢迎通过我们的<a href=\"http://zjucompass.shuailong.me\">官方" +
                        "网站</a>了解更多信息。也可以通过APP内的意见反馈将您的宝贵意见提交到我们的系统后台。"));
        answer4.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
