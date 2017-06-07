package wuchen.com.citypicker;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by 巫晨 on 2017/6/4.
 */

public class MyApplication extends Application {

    public LocationClient mLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        mLocationClient = new LocationClient(getApplicationContext());
    }

    public LocationClient getLocationClient() {
        return mLocationClient;
    }
}
