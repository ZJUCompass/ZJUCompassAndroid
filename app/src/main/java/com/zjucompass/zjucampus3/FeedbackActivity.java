package com.zjucompass.zjucampus3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Send feedback to the system
 */
public class FeedbackActivity extends ActionBarActivity {
    private String mFeedbackUrl;

    // UI Reference
    private View mProgressView;
    private View mFeedbackView;
    private EditText mFeedbackContentEdit;
    private EditText mEmailEdit;
    private EditText mPhoneEdit;
    private SendFeedbackTask sendTask;

    // Debug
    private static final String TAG = "FeedbackActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Find UI reference
        mFeedbackContentEdit = (EditText) findViewById(R.id.feedback_content);
        mEmailEdit = (EditText) findViewById(R.id.feedback_email);
        mPhoneEdit = (EditText) findViewById(R.id.feedback_phone);
        mProgressView = findViewById(R.id.progress_feedback);
        mFeedbackView = findViewById(R.id.feedback_view);

        Button mSubmitButton = (Button) findViewById(R.id.submit_feedback);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedbackContentEdit.length() == 0) {
                    return;
                }
                String feedBackContent = mFeedbackContentEdit.getText().toString();
                String email = mEmailEdit.getText().toString();
                String phone = mPhoneEdit.getText().toString();
                sendTask = new SendFeedbackTask(feedBackContent, email, phone);
                sendTask.execute(mFeedbackUrl);
                showProgress(true);
            }
        });

        String domain = Configuration.DOMAIN;
        mFeedbackUrl = "http://" + domain + ":5000/api/feedback";
    }

    /* Show sending progress or not */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        if(show) {
            mFeedbackView.setAlpha((float) 0.3);
        }
        else{
            mFeedbackView.setAlpha((float) 1.0);
        }
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



    /* Send Feedback task */
    private class SendFeedbackTask extends AsyncTask<String, Void, Boolean> {
        private String mFeedback, mEmail, mPhone;
        public SendFeedbackTask(String feedbackContent, String email, String phone){
            mFeedback = feedbackContent;
            mEmail = email;
            mPhone = phone;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean success = false;

            JSONObject param = new JSONObject();
            try {
                param.put("feedback", mFeedback);
                param.put("email", mEmail);
                param.put("phone", mPhone);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            StringEntity paramEntity = null;
            try {
                paramEntity = new StringEntity(param.toString(), "utf-8");
                paramEntity.setContentEncoding("HTTP.UTF_8");
                paramEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpPost request = new HttpPost(params[0]);
            request.setEntity(paramEntity);
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
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            mFeedbackContentEdit.setText("");
            mEmailEdit.setText("");
            mPhoneEdit.setText("");

            if(success) {
                Toast.makeText( getApplicationContext(), getString(R.string.message_sent_success),
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText( getApplicationContext(), getString(R.string.message_sent_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}
