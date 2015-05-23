package com.zjucompass.zjucampus3;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MatterDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matter_detail);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getBundleExtra("bundle");
        String matteraName = bundle.getString("matter_name");
        String matterDescription = bundle.getString("matter_description");
        String matterLink = bundle.getString("matter_link");

        TextView matterNameView = (TextView) findViewById(R.id.matter_detail_name);
        TextView matterDescriptionView = (TextView) findViewById(R.id.matter_detail_description);
        TextView matterLinkView = (TextView) findViewById(R.id.matter_detail_link);
        matterNameView.setText(matteraName);
        matterDescriptionView.setText(matterDescription);
        matterLinkView.setText(Html.fromHtml("<a href=\""+matterLink+"\">"+"原始网页"+"</a>"));
        matterLinkView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_matter_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_correct_matter) {
            Intent intent = new Intent(this, CorrectMatterActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
