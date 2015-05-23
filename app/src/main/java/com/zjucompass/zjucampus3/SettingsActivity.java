package com.zjucompass.zjucampus3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class SettingsActivity extends ActionBarActivity {

    private View mProgressView;
    private View mCheckUpdateView;
    private View mFaqView;
    private View mFeedbackView;

    private static String TAG = "SettingsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mProgressView = findViewById(R.id.settings_check_upadte_progress);
        mCheckUpdateView = findViewById(R.id.settings_check_update);
        mFaqView = findViewById(R.id.settings_faq);
        mFeedbackView = findViewById(R.id.settings_feedback);

        mCheckUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String domain = Configuration.DOMAIN;
                String mCheckUpdateUrl = "http://" + domain + ":5000/api/check_update";
                new CheckNewVersionTask().execute(mCheckUpdateUrl);
                showProgress(true);
            }
        });

        mFaqView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FaqActivity.class);
                startActivity(intent);
            }
        });

        mFeedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE
                                : View.GONE);
                    }
                });
    }

    public class CheckNewVersionTask extends AsyncTask<String, Void, Boolean> {
        boolean mIsLatest;
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean success = false;

            HttpGet request = new HttpGet(params[0]);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                response = client.execute(request);
                Log.d(TAG, request.getURI().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(response != null) {
                Log.d(TAG, String.valueOf(response.getStatusLine().getStatusCode()));
                if (response.getStatusLine().getStatusCode()== 200) {
                    String retSrc;
                    try {
                        retSrc = EntityUtils.toString(response.getEntity());
                        Log.d(TAG, retSrc);
                        success = true;
                        mIsLatest = retSrc.equals("1.0.0");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                Log.d(TAG, "No response from server.");
            }
            return success;
        }

        @Override
        protected void onPostExecute(final Boolean newest) {
            showProgress(false);
            Toast toast;
            if(mIsLatest) {
                toast = Toast.makeText(getApplicationContext(),
                        "已是最新版本",
                        Toast.LENGTH_SHORT);
            }
            else{
                toast = Toast.makeText(getApplicationContext(),
                        "请访问http://zjucompass.shuailong.me更新至最新版本",
                        Toast.LENGTH_SHORT);
            }
            toast.show();
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}
