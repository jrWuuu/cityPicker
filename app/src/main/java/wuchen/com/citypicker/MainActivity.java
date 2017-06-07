package wuchen.com.citypicker;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import wuchen.com.citypicker.adapter.MyAdapter;
import wuchen.com.citypicker.bean.CityBean;
import wuchen.com.citypicker.utils.CityUtils;
import wuchen.com.citypicker.utils.MyToast;
import wuchen.com.citypicker.utils.PermissionUtils;
import wuchen.com.citypicker.view.QuickIndexBar;

public class MainActivity extends AppCompatActivity implements QuickIndexBar.onLetterChangeLIstener, MyAdapter.OnItemClickListener, MyAdapter.OnThreeHeadClickListener {

    @InjectView(R.id.rv)
    RecyclerView mRv;
    @InjectView(R.id.quickIndexBar)
    QuickIndexBar mQuickIndexBar;
    @InjectView(R.id.dialog)
    TextView mDialog;
    private String TAG = "tag";
    private MyAdapter mAdapter;
    private ArrayList<CityBean> mCitys;
    private LinearLayoutManager mLayout;
    private int mPos;
    private Handler mHandler = new Handler();
    private Object mLocation;
    private LocationClient mLocationClient;
    private Object mPosition;
    private GeoCoder mGeoCoder;
    private PoiSearch mPoiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        //请求权限
        requestPermission();
        getLocation();
        copyDBtoFiles();
        //数据库文件保存到本地之后我们需要去取出数据库中所有的信息
        mQuickIndexBar.setOnLetterChangeLIstener(this);
        mCitys = CityUtils.getCitys(this);
        Collections.sort(mCitys);
        /*mAdapter = new RVAdpater(this, mCitys);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(new StickyRecyclerHeadersDecoration(mAdapter));
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setOnRVClickListener(this);*/
        mLayout = new LinearLayoutManager(this);
        mRv.setLayoutManager(mLayout);
        mAdapter = new MyAdapter(this, mCitys);
        mRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnThreeHeadClickListener(this);
    }

    private void requestPermission() {
        //请求权限
        String[] permissions = PermissionUtils.getPermissions(this);
        if (permissions.length != 0) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    finish();
                    break;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void copyDBtoFiles() {
        //把assert下的数据库拷贝到file目录下面
        InputStream in = null;
        FileOutputStream fos = null;
        File file = new File(getFilesDir(), "citys.db");
        if (file.exists()) {
            return;
        }
        try {
            in = getAssets().open("citys.db");
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] arr = new byte[1024];
            while ((len = in.read(arr)) != -1) {
                fos.write(arr, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
        }
    }

    @Override
    public void onLetterChange(String letter) {
        for (int i = 0; i < mCitys.size(); i++) {
            char c = mCitys.get(i).getPinYIn().charAt(0);
            if (TextUtils.equals(c + "", letter)) {
                Log.d(TAG, "onLetterChange:----" + letter + "===" + c);
                mPos = i;
                break;
            }
        }
        mLayout.scrollToPositionWithOffset(mPos, 0);
        mDialog.setText(letter);
        mDialog.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new TimerTask() {
            @Override
            public void run() {
                mDialog.setVisibility(View.GONE);
            }
        }, 1000);
    }

    @Override
    public void onItemClick(String string) {
        SharedPreferences history = getSharedPreferences("history", Context.MODE_PRIVATE);
        HashSet<String> citys = (HashSet<String>) history.getStringSet("citys", null);
        if (citys == null) {
            citys = new LinkedHashSet<>();
        }
        if (!citys.contains(string)) {
            SharedPreferences.Editor edit = history.edit();
            citys.add(string);
            edit.putStringSet("citys", citys);
            edit.apply();
            mAdapter.notifyItemChanged(1);
        }
        getPosition(string);
    }

    @Override
    public void onHeadClick(String string) {
        MyToast.show(this, string);
    }

    public void getLocation() {
        //百度地图请求定位
        MyApplication application = (MyApplication) getApplication();
        mLocationClient = application.getLocationClient();
        //注册监听函数
        mLocationClient.registerLocationListener(new MyLocationListener());
        initLocation();
        mLocationClient.start();
    }

    /**
     * 根据城市名称获取经纬度等信息
     */
    public void getPosition(String name) {
        // 创建地理编码检索实例
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.geocode(new GeoCodeOption().city(name).address(name));
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
    }

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
        // 反地理编码查询结果回调函数
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检测到结果
                Toast.makeText(MainActivity.this, "抱歉，未能找到结果",
                        Toast.LENGTH_SHORT).show();
            }
            MyToast.show(MainActivity.this, "地址__:" + result.getAddress() + " : \r坐标" + result.getLocation().latitude + ":" + result.getLocation().longitude);
        }

        // 地理编码查询结果回调函数
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null
                    || result.error != SearchResult.ERRORNO.NO_ERROR) {
                // 没有检测到结果
                return;
            }
            MyToast.show(MainActivity.this, "地址 ： " + result.getAddress() + "\n坐标" + result.getLocation().latitude + ":" + result.getLocation().longitude);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }

    @Override
    public void onFirstClick(String string) {
        getPosition(string);
    }

    @Override
    public void onSecondClick(String string) {
        getPosition(string);
    }

    @Override
    public void onThirdclick(String string) {
        getPosition(string);
    }

    public class MyLocationListener implements BDLocationListener {

        private String mCity;
        private double mLatitude;
        private double mAltitude;

        @Override
        public void onReceiveLocation(BDLocation location) {
            mCity = location.getCity();
            mLatitude = location.getLatitude();
            mAltitude = location.getAltitude();
            runOnUiThread(new TimerTask() {
                @Override
                public void run() {
                    mAdapter.setLocation(mCity);
                    Log.i("tag", "onReceiveLocation: " + mCity + mLatitude + mAltitude);
                }
            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

}
