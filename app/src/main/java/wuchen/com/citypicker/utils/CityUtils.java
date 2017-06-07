package wuchen.com.citypicker.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import wuchen.com.citypicker.bean.CityBean;

/**
 * Created by 巫晨 on 2017/6/3.
 */

public class CityUtils {

    public static ArrayList<CityBean> getCitys(Context context) {
        ArrayList<CityBean> cityBeens = new ArrayList<>();
        File file = new File(context.getFilesDir(), "citys.db");
        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select name from city;", null);
        while (cursor.moveToNext()) {
            cityBeens.add(new CityBean(cursor.getString(0)));
            cursor.moveToNext();
        }
        cursor.close();
        return cityBeens;
    }

}
