package com.zjucompass.zjucampus3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class TeacherDetailActivity extends ActionBarActivity {

    private Bundle mBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_detail);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mBundle = getIntent().getBundleExtra("bundle");
        String id = mBundle.getString("teacher_id", getString(R.string.no_data));
        if(id.equals("null")) id = "";
        String name = mBundle.getString("teacher_name", getString(R.string.no_data));
        if(name.equals("null")) name = "";
        String dept = mBundle.getString("teacher_dept", getString(R.string.no_data));
        if(dept.equals("null")) dept = "";
        String title = mBundle.getString("teacher_title", getString(R.string.no_data));
        if(title.equals("null")) title = "";
        String phone = mBundle.getString("teacher_phone", getString(R.string.no_data));
        if(phone.equals("null")) phone = "";
        String email = mBundle.getString("teacher_email", getString(R.string.no_data));
        if(email.equals("null")) email = "";
        String building = mBundle.getString("teacher_building", getString(R.string.no_data));
        if(building.equals("null")) building = "";
        String room = mBundle.getString("teacher_room", getString(R.string.no_data));
        if(room.equals("null")) room = "";
        String research = mBundle.getString("teacher_research", getString(R.string.no_data));
        if(research.equals("null")) research = "";

        ImageView teacherImgView = (ImageView) findViewById(R.id.teacher_detail_img);
        TextView teacherNameView = (TextView) findViewById(R.id.teacher_detail_name);
        TextView teacherDeptView = (TextView) findViewById(R.id.teacher_detail_dept);
        TextView teacherTitleView = (TextView) findViewById(R.id.teacher_detail_title);
        TextView teacherPhoneView = (TextView) findViewById(R.id.teacher_detail_phone);
        TextView teacherEmailView = (TextView) findViewById(R.id.teacher_detail_email);
        TextView teacherBuildingView = (TextView) findViewById(R.id.teacher_detail_building);
        TextView teacherResearchView = (TextView) findViewById(R.id.teacher_detail_research);

        ImageView callTeacherView = (ImageView) findViewById(R.id.call_teacher);
        ImageView emailTeacherView = (ImageView) findViewById(R.id.email_teacher);

        String imgId = "t_"+id;
        teacherImgView.setImageResource(getResources().
                getIdentifier(imgId, "drawable", "com.zjucompass.zjucampus3"));
        teacherNameView.setText(name);
        teacherTitleView.setText(title);
        teacherDeptView.setText(dept);
        teacherEmailView.setText(email);
        teacherPhoneView.setText(phone);
        teacherBuildingView.setText(building + " " + room);
        teacherResearchView.setText(research);

        setTitle(name);

        final String finalPhone = phone;
        callTeacherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                String uri = "tel:" + finalPhone;
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        final String finalEmail = email;
        emailTeacherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{finalEmail});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT   , "");
                startActivity(Intent.createChooser(i, "发送邮件..."));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_correct_teacher) {
            Intent intent = new Intent(this, CorrectTeacherActivity.class);
            intent.putExtra("bundle", mBundle);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
