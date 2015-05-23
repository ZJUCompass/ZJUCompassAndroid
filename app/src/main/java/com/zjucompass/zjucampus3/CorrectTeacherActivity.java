package com.zjucompass.zjucampus3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


public class CorrectTeacherActivity extends ActionBarActivity {

    private String mCorrectTeacherUrl;

    // UI Reference
    private View mProgressView;
    private View mCorrectTeacherView;
    private EditText mNameEdit;
    private EditText mTitleEdit;
    private EditText mPhoneEdit;
    private EditText mEmailEdit;
    private EditText mLocationEdit;
    private EditText mResearchEdit;
    private EditText mDeptEdit;

    private String mId, mName, mTitle, mDept, mPhone, mEmail, mBuilding, mRoom, mResearch;

    private SendCorrectTeacherTask sendTask;

    private static String TAG = "CorrectTeacherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_teacher);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getBundleExtra("bundle");
        mId = bundle.getString("teacher_id", getString(R.string.no_data));
        if(mId.equals("null")) mId = "";
        mName = bundle.getString("teacher_name", getString(R.string.no_data));
        if(mName.equals("null")) mName = "";
        mTitle = bundle.getString("teacher_title", getString(R.string.no_data));
        if(mTitle.equals("null")) mTitle = "";
        mDept = bundle.getString("teacher_dept", getString(R.string.no_data));
        if(mDept.equals("null")) mDept = "";
        mPhone = bundle.getString("teacher_phone", getString(R.string.no_data));
        if(mPhone.equals("null")) mPhone = "";
        mEmail = bundle.getString("teacher_email", getString(R.string.no_data));
        if(mEmail.equals("null")) mEmail = "";
        mBuilding = bundle.getString("teacher_building", getString(R.string.no_data));
        if(mBuilding.equals("null")) mBuilding = "";
        mRoom = bundle.getString("teacher_room", getString(R.string.no_data));
        if(mRoom.equals("null")) mRoom = "";
        mResearch = bundle.getString("teacher_research", getString(R.string.no_data));
        if(mResearch.equals("null")) mResearch = "";

        ImageView teacherImgView = (ImageView) findViewById(R.id.correct_teacher_detail_img);
        mNameEdit = (EditText) findViewById(R.id.correct_teacher_detail_name);
        mTitleEdit = (EditText) findViewById(R.id.correct_teacher_detail_title);
        mDeptEdit = (EditText) findViewById(R.id.correct_teacher_detail_dept);
        mPhoneEdit = (EditText) findViewById(R.id.correct_teacher_detail_phone);
        mEmailEdit = (EditText) findViewById(R.id.correct_teacher_detail_email);
        mLocationEdit = (EditText) findViewById(R.id.correct_teacher_detail_building);
        mResearchEdit = (EditText) findViewById(R.id.correct_teacher_detail_research);

        mProgressView = findViewById(R.id.progress_correct_teacher);
        mCorrectTeacherView = findViewById(R.id.correct_teacher);

        String imgId = "t_"+mId;
        teacherImgView.setImageResource(getResources().
                getIdentifier(imgId, "drawable", "com.zjucompass.zjucampus3"));
        mNameEdit.setText(mName);
        mTitleEdit.setText(mTitle);
        mDeptEdit.setText(mDept);
        mEmailEdit.setText(mEmail);
        mPhoneEdit.setText(mPhone);
        mLocationEdit.setText(mBuilding + " " + mRoom);
        mResearchEdit.setText(mResearch);

        setTitle(mName + "的纠错页面");

        Button mSubmitButton = (Button) findViewById(R.id.submit_correct_teacher);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                Bundle bundle = new Bundle();

                String name = CorrectTeacherActivity.this.mNameEdit.getText().toString();
                String title = CorrectTeacherActivity.this.mTitleEdit.getText().toString();
                String dept = CorrectTeacherActivity.this.mDeptEdit.getText().toString();
                String email = CorrectTeacherActivity.this.mEmailEdit.getText().toString();
                String phone = CorrectTeacherActivity.this.mPhoneEdit.getText().toString();
                String location = CorrectTeacherActivity.this.mLocationEdit.getText().toString();
                String research = CorrectTeacherActivity.this.mResearchEdit.getText().toString();

                bundle.putString("name", name);
                bundle.putString("id", mId);

                if(!title.equals(mTitle)){
                    bundle.putString("title", title);
                }
                if(!dept.equals(mDept)){
                    bundle.putString("dept", dept);
                }
                if(!email.equals(mEmail)){
                    bundle.putString("email", email);
                }
                if(!phone.equals(mPhone)){
                    bundle.putString("phone", phone);
                }
                if(!location.equals(mBuilding + " " + mRoom)){
                    bundle.putString("location", location);
                }
                if(!research.equals(mResearch)){
                    bundle.putString("research", research);
                }

                sendTask = new SendCorrectTeacherTask(bundle);
                sendTask.execute(mCorrectTeacherUrl);
                showProgress(true);
            }
        });

        String domain = Configuration.DOMAIN;
        //TODO: set proper url
        mCorrectTeacherUrl = "http://" + domain + ":5000/api/correct_teacher";
    }

    /* Show sending progress or not */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        if(show) {
            mCorrectTeacherView.setAlpha((float) 0.3);
        }
        else{
            mCorrectTeacherView.setAlpha((float) 1.0);
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



    private class SendCorrectTeacherTask extends AsyncTask<String, Void, Boolean> {
        private String mId, mName, mTitle, mDept, mPhone, mEmail, mLocation, mResearch;
        public SendCorrectTeacherTask(Bundle bundle){
            mId = bundle.getString("id", "");
            mName = bundle.getString("name", "");
            mTitle = bundle.getString("title", "");
            mDept = bundle.getString("dept", "");
            mPhone = bundle.getString("phone", "");
            mEmail = bundle.getString("email", "");
            mLocation = bundle.getString("location", "");
            mResearch = bundle.getString("research", "");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean success = false;

            JSONObject param = new JSONObject();
            try {
                param.put("id", mId);
                param.put("name", mName);
                param.put("title", mTitle);
                param.put("dept", mDept);
                param.put("email", mEmail);
                param.put("phone", mPhone);
                param.put("location", mLocation);
                param.put("research", mResearch);

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
            request.addHeader("Content-Type", "application/json;charset=utf-8");
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

            if(success) {
                Toast.makeText(getApplicationContext(), getString(R.string.message_sent_success),
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
