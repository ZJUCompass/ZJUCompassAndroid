package com.zjucompass.zjucampus3;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.csvreader.CsvReader;
import com.umeng.analytics.MobclickAgent;
import com.zjucompass.zjucampus3.widget.ContactItemInterface;
import com.zjucompass.zjucampus3.widget.ContactListViewImpl;
import com.zjucompass.zjucampus3.widget.pinyin.PinYin;

import java.nio.charset.Charset;
import java.util.ArrayList;

//import com.umeng.analytics.MobclickAgent;

public class TeacherFragment extends Fragment {

    private ArrayList<Teacher> mTeacherInfoList = new ArrayList<>();
    private ArrayList<Teacher> mTeacherInfoDeptList = new ArrayList<>();
    private ArrayList<Teacher> mFilterList = new ArrayList<>();
    private TeacherListAdapter mAdapter;
    private ContactListViewImpl mListView;
    private TextView mContactCountView;

    private Object searchLock = new Object();
    boolean inSearchMode = false;
    private SearchListTask curSearchTask = null;

    //debug
    private static final String TAG = "TeacherFragment";

    public static TeacherFragment newInstance() {
        return new TeacherFragment();
    }

    public TeacherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_teacher, container, false);
        mListView = (ContactListViewImpl) rootView.findViewById(R.id.teacher_list);

        mAdapter = new TeacherListAdapter(getActivity(), R.layout.fragment_teacher_item,
                mTeacherInfoDeptList);

        mListView.setFastScrollEnabled(true);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TeacherDetailActivity.class);
                Teacher t = (Teacher) parent.getItemAtPosition(position);

                String t_id = t.getId();
                String name = t.getName();
                String dept = t.getDept();
                String title = t.getTitle();
                String phone = t.getPhone();
                String email = t.getEmail();
                String building = t.getBuilding();
                String room = t.getRoom();
                String research = t.getResearch();

                Bundle bundle = new Bundle();
                bundle.putString("teacher_id", t_id);
                bundle.putString("teacher_name", name);
                bundle.putString("teacher_dept", dept);
                bundle.putString("teacher_title", title);
                bundle.putString("teacher_phone", phone);
                bundle.putString("teacher_email", email);
                bundle.putString("teacher_building", building);
                bundle.putString("teacher_room", room);
                bundle.putString("teacher_research", research);

                intent.putExtra("bundle", bundle);

                startActivity(intent);
            }
        });

        LayoutInflater inflater2 = LayoutInflater.from(getActivity());
        mContactCountView = (TextView) inflater2.inflate(R.layout.fragment_teacher_footer, null);
        mListView.addFooterView(mContactCountView);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        populateData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.menu_teacher, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_teacher);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("输入教师姓名...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchString) {
                if (curSearchTask != null
                        && curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
                {
                    try
                    {
                        curSearchTask.cancel(true);
                    } catch (Exception e)
                    {
                        Log.i(TAG, "Fail to cancel running search task");
                    }

                }
                curSearchTask = new SearchListTask();
                curSearchTask.execute(searchString);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchString) {
                if (curSearchTask != null
                        && curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
                {
                    try
                    {
                        curSearchTask.cancel(true);
                    } catch (Exception e)
                    {
                        Log.i(TAG, "Fail to cancel running search task");
                    }

                }
                curSearchTask = new SearchListTask();
                curSearchTask.execute(searchString);
                return true;
            }

        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_teacher:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TeacherPage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TeacherPage");
    }

    private void populateData(){
        try {
            ArrayList<String[]> csvList = new ArrayList<String[]>(); //用来保存数据
            CsvReader reader = new CsvReader(getResources().openRawResource(R.raw.teacher_info),
                    '\t', Charset.forName("UTF-8"));

            reader.readHeaders(); // 跳过表头
            while(reader.readRecord()){ //逐行读入除表头的数据
                csvList.add(reader.getValues());
            }
            reader.close();

            for(int row=0;row<csvList.size();row++){
                String id = csvList.get(row)[0];
                String name = csvList.get(row)[1];
                String dept = csvList.get(row)[2];
                String title = csvList.get(row)[3];
                String phone = csvList.get(row)[4];
                String email = csvList.get(row)[5];
                String building = csvList.get(row)[6];
                String room = csvList.get(row)[7];
                String research = csvList.get(row)[8];

                Teacher teacher = new Teacher(id, name, dept, title, phone, email, building,
                        room, research, PinYin.getPinYin(name));

                mTeacherInfoList.add(teacher);
            }

        }catch(Exception ex){
            Log.e(TAG, ex.toString());
        }
    }

    public void filterByDepartment(String dept) {
        mTeacherInfoDeptList.clear();

        if (!dept.equals("全部")){
            for (int i = 0; i < mTeacherInfoList.size(); ++i) {
                if (mTeacherInfoList.get(i).getDept().equals(dept)) {
                    mTeacherInfoDeptList.add(mTeacherInfoList.get(i));
                }
            }
        }
        else{
            for (int i = 0; i < mTeacherInfoList.size(); ++i){
                mTeacherInfoDeptList.add(mTeacherInfoList.get(i));
            }
        }

        TeacherListAdapter adapter = new TeacherListAdapter(getActivity(),
                R.layout.fragment_teacher_item, mTeacherInfoDeptList);
        mListView.setAdapter(adapter);

        mContactCountView.setText(mTeacherInfoDeptList.size() + "位联系人");
    }

    private class SearchListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mFilterList.clear();

            String keyword = params[0];

            inSearchMode = (keyword.length() > 0);

            if (inSearchMode) {
                // get all the items matching this
                for (ContactItemInterface item : mTeacherInfoDeptList) {
                    Teacher teacher = (Teacher) item;

                    boolean isPinyin = teacher.getName().toUpperCase().contains(keyword);
                    boolean isChinese = teacher.getPinyinName().contains(keyword);

                    if (isPinyin || isChinese) {
                        mFilterList.add(teacher);
                    }

                }

            }
            return null;
        }

        protected void onPostExecute(String result) {

            synchronized (searchLock) {

                if (inSearchMode) {
                    TeacherListAdapter adapter = new TeacherListAdapter(getActivity(),
                            R.layout.fragment_teacher_item, mFilterList);
                    adapter.setInSearchMode(true);
                    mListView.setInSearchMode(true);
                    mListView.setAdapter(adapter);
                    mContactCountView.setText(mFilterList.size() + "个搜索结果");
                } else {
                    TeacherListAdapter adapter = new TeacherListAdapter(getActivity(),
                            R.layout.fragment_teacher_item, mTeacherInfoDeptList);
                    adapter.setInSearchMode(false);
                    mListView.setInSearchMode(false);
                    mListView.setAdapter(adapter);
                    mContactCountView.setText(mTeacherInfoDeptList.size() + "位联系人");
                }
            }

        }
    }
}
