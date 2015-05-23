package com.zjucompass.zjucampus3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    SectionsPagerAdapter mSectionsPagerAdapter;
    NoSlideViewPager mViewPager;
    String mDepartment;

    MapFragment mapFragment = MapFragment.newInstance();
    TeacherFragment teacherFragment = TeacherFragment.newInstance();
    MatterFragment matterFragment = MatterFragment.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        Spinner spinner = new Spinner(this);

        final String schools[];
        String schoolstr = "全部\n" +
                "经济学院\n" +
                "光华法学院\n" +
                "教育学院\n" +
                "人文学院\n" +
                "外国语言文化与国际交流学院\n" +
                "理学院\n" +
                "生命科学学院\n" +
                "电气工程学院\n" +
                "建筑工程学院\n" +
                "生物系统工程与食品科学学院\n" +
                "环境与资源学院\n" +
                "生物医学工程与仪器科学学院\n" +
                "农业与生物技术学院\n" +
                "动物科学学院\n" +
                "医学院\n" +
                "药学院\n" +
                "管理学院\n" +
                "计算机科学与技术学院\n" +
                "软件学院\n" +
                "公共管理学院\n" +
                "传媒与国际文化学院\n" +
                "航空航天学院\n" +
                "思想政治理论教学科研部\n" +
                "农业生命环境学部办公室\n" +
                "海洋科学与工程学系\n" +
                "高分子科学与工程学系\n" +
                "机械工程学系\n" +
                "材料科学与工程学系\n" +
                "能源工程学系\n" +
                "化学工程与生物工程学系\n" +
                "光电信息工程学系\n" +
                "信息与电子工程学系\n" +
                "控制科学与工程学系";

        schools = schoolstr.split("\n");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, schools);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDepartment = schools[position];
                teacherFragment.filterByDepartment(mDepartment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(18);
        mDepartment = schools[18];
        actionBar.setCustomView(spinner);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (NoSlideViewPager) findViewById(R.id.pager);
        mViewPager.setNoScroll(true);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        // UMeng
        AnalyticsConfig.enableEncrypt(true);
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.openActivityDurationTrack(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_feedback) {
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return mapFragment;
            }
            else if(position == 1){
                return teacherFragment;
            }
            else{
                return matterFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3);
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen");
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainSceen");
        MobclickAgent.onPause(this);
    }

}
