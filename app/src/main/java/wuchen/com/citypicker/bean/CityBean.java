package wuchen.com.citypicker.bean;

import android.support.annotation.NonNull;

import wuchen.com.citypicker.utils.PinYinUtils;

/**
 * Created by 巫晨 on 2017/6/3.
 */

public class CityBean implements Comparable<CityBean>{

    private String cityName;
    private String pinYIn;

    public CityBean(String cityName) {
        this.cityName = cityName;
        this.pinYIn = PinYinUtils.getPinYin(cityName);
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setPinYIn(String pinYIn) {
        this.pinYIn = pinYIn;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPinYIn() {
        return pinYIn;
    }

    @Override
    public int compareTo(@NonNull CityBean bean) {
        return this.getPinYIn().compareTo(bean.getPinYIn());
    }
}
