package com.zjucompass.zjucampus3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


public class MapFragment extends Fragment implements LocationSource, AMapLocationListener,
        AMap.OnMarkerClickListener, AMap.InfoWindowAdapter, AdapterView.OnItemSelectedListener,
        PoiSearch.OnPoiSearchListener, AMap.OnMapClickListener, AMap.OnInfoWindowClickListener,
        View.OnClickListener {
    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;

    private String keyWord;
    private PoiResult poiResult; // poi返回的结果
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp = new LatLonPoint(30.268188, 120.122012);// 默认浙大玉泉
    private Marker locationMarker; // 选择的点
    private PoiSearch poiSearch;
    private PoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private Marker detailMarker;// 显示Marker的详情

    private ProgressDialog progDialog = null;// 搜索时进度条

    private Object searchLock = new Object();
    boolean inSearchMode = false;

    //debug
    private static final String TAG = "MapFragment";

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        if(keyWord.isEmpty()){
            return;
        }

        showProgressDialog();// 显示进度框

        aMap.setOnMapClickListener(null);// 进行poi搜索时清除掉地图点击事件
        query = new PoiSearch.Query(keyWord, "", "0571");
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(100);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页

//        query.setLimitDiscount(false);
//        query.setLimitGroupbuy(false);

        if (lp != null) {
            poiSearch = new PoiSearch(getActivity(), query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 2000, true));//
//            // 设置搜索区域为以lp点为圆心，其周围2000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        Toast.makeText(getActivity(), infomation, Toast.LENGTH_SHORT).show();
    }

    /**
     * POI详情回调
     */
    @Override
    public void onPoiItemDetailSearched(PoiItemDetail result, int rCode) {

    }


    /**
     * POI搜索回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(getActivity(), "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getActivity(), "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(getActivity(), "搜索失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(getActivity(), "key验证无效！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "未知错误，请稍后重试！错误码为：" + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(LatLng latng) {
        locationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker_red))
                .position(latng).title("点击选择为中心点"));
        locationMarker.showInfoWindow();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        locationMarker.hideInfoWindow();
        lp = new LatLonPoint(locationMarker.getPosition().latitude,
                locationMarker.getPosition().longitude);
        locationMarker.destroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_map);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("输入地点...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchString) {
                keyWord = searchString;
                doSearchQuery();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchString) {
                keyWord = searchString;
                doSearchQuery();
                return true;
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_map:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        mUiSettings = aMap.getUiSettings();
        setUpMap();
        return rootView;
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.my_location));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        aMap.setInfoWindowAdapter(this);// 添加显示infowindow监听事件

        // aMap.setMyLocationType()
        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        MobclickAgent.onPageStart("MapPage");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
        MobclickAgent.onPageEnd("MapPage");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            double geoLat = aMapLocation.getLatitude();
            double geoLng = aMapLocation.getLongitude();
            String cityCode = aMapLocation.getCityCode();
            String cityName = aMapLocation.getCity();
            Log.d(TAG, "Latitude: " + geoLat + ", Longtitude: " + geoLng + ", CityCode: " + cityCode +
            ", City: " + cityName);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.setGpsEnable(false);
            mAMapLocationManager.requestLocationUpdates(
                    LocationProviderProxy.AMapNetwork, 2000, 10, this);
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destory();
        }
        mAMapLocationManager = null;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        // 调起高德地图app
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startURI(marker);
            }
        });
        return view;
    }

    /**
     * 通过URI方式调起高德地图app
     */
    public void startURI(Marker marker) {
        if (getAppIn()) {
            Intent intent = new Intent(
                    "android.intent.action.VIEW",
                    android.net.Uri
                            .parse("androidamap://viewMap?sourceApplication="
                                    + getApplicationName() + "&poiname="
                                    + marker.getTitle() + "&lat="
                                    + marker.getPosition().latitude + "&lon="
                                    + marker.getPosition().longitude + "&dev=0"));
            intent.setPackage("com.autonavi.minimap");
            startActivity(intent);
        } else {
            String url = "http://mo.amap.com/?dev=0&q="
                    + marker.getPosition().latitude + ","
                    + marker.getPosition().longitude + "&name="
                    + marker.getTitle();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

    }
    /**
     * 判断高德地图app是否已经安装
     */
    public boolean getAppIn() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(
                    "com.autonavi.minimap", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        // 本手机没有安装高德地图app
        if (packageInfo != null) {
            return true;
        }
        // 本手机成功安装有高德地图app
        else {
            return false;
        }
    }

    /**
     * 获取当前app的应用名字
     */
    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getActivity().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(
                    getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager
                .getApplicationLabel(applicationInfo);
        return applicationName;
    }

//    @Override
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        String newText = s.toString().trim();
//        Inputtips inputTips = new Inputtips(getActivity(),
//                new Inputtips.InputtipsListener() {
//
//                    @Override
//                    public void onGetInputtips(List<Tip> tipList, int rCode) {
//                        if (rCode == 0) {// 正确返回
//                            List<String> listString = new ArrayList<String>();
//                            for (int i = 0; i < tipList.size(); i++) {
//                                listString.add(tipList.get(i).getName());
//                            }
//                            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
//                                    getActivity(),
//                                    R.layout.route_inputs, listString);
//                            searchText.setAdapter(aAdapter);
//                            aAdapter.notifyDataSetChanged();
//                        }
//                    }
//                });
//        try {
//            inputTips.requestInputtips(newText, editCity.getText().toString());// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
//
//        } catch (AMapException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

}
