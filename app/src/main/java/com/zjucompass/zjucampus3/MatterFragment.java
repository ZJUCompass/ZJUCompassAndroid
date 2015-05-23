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
import android.widget.ListView;

import com.csvreader.CsvReader;
import com.umeng.analytics.MobclickAgent;
import com.zjucompass.zjucampus3.widget.pinyin.PinYin;

import java.nio.charset.Charset;
import java.util.ArrayList;


public class MatterFragment extends Fragment {

    private ArrayList<Matter> mMatterInfoList = new ArrayList<>();
    private ArrayList<Matter> mFilterList = new ArrayList<>();
    private MatterListAdapter mAdapter;
    private ListView mListView;

    private Object searchLock = new Object();
    boolean inSearchMode = false;
    private SearchListTask curSearchTask = null;

    //debug
    private static final String TAG = "MatterFragment";

    public static MatterFragment newInstance() {
        return new MatterFragment();
    }

    public MatterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_matter, container, false);
        mListView = (ListView) rootView.findViewById(R.id.matter_list);

        mAdapter = new MatterListAdapter(getActivity(), R.layout.fragment_matter_item,
                R.id.matter_name, mMatterInfoList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Matter m = (Matter) parent.getItemAtPosition(position);

                String matterName = m.getName();
                String matterDescription = m.getDescription();
                String matterLink = m.getLink();

                Intent intent = new Intent(getActivity(), MatterDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("matter_name", matterName);
                bundle.putString("matter_description", matterDescription);
                bundle.putString("matter_link", matterLink);
                intent.putExtra("bundle", bundle);

                startActivity(intent);
            }
        });


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
        inflater.inflate(R.menu.menu_matter, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_matter);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("输入事项...");

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
            case R.id.action_search_matter:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MatterPage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MatterPage");
    }

    private void populateData(){
        try {
            ArrayList<String[]> csvList = new ArrayList<String[]>(); //用来保存数据
            CsvReader reader = new CsvReader(getResources().openRawResource(R.raw.matter_info),
                    '\t', Charset.forName("UTF-8"));

            reader.readHeaders(); // 跳过表头

            while(reader.readRecord()){ //逐行读入除表头的数据
                csvList.add(reader.getValues());
            }
            reader.close();

            for(int row=0;row<csvList.size();row++){
                String name = csvList.get(row)[0];
                String description = csvList.get(row)[7];
                String link = csvList.get(row)[19];
                Matter m  = new Matter(name, description, link);
                mMatterInfoList.add(m);
            }

        }catch(Exception ex){
            Log.e(TAG, ex.toString());
        }
    }


    private class SearchListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            mFilterList.clear();

            String keyword = params[0];

            inSearchMode = (keyword.length() > 0);

            if (inSearchMode) {
                // get all the items matching this
                for (Matter item : mMatterInfoList) {

                    boolean isPinyin = item.getName().toUpperCase().contains(keyword);
                    boolean isChinese = PinYin.getPinYin(item.getName()).contains(keyword);

                    if (isPinyin || isChinese) {
                        mFilterList.add(item);
                    }

                }

            }
            return null;
        }

        protected void onPostExecute(String result) {

            synchronized (searchLock) {

                if (inSearchMode) {
                    MatterListAdapter adapter = new MatterListAdapter(getActivity(),
                            R.layout.fragment_matter_item, R.id.matter_name, mFilterList);
                    mListView.setAdapter(adapter);
                } else {
                    MatterListAdapter adapter = new MatterListAdapter(getActivity(),
                            R.layout.fragment_matter_item, R.id.matter_name, mMatterInfoList);
                    mListView.setAdapter(adapter);
                }
            }

        }
    }
}
