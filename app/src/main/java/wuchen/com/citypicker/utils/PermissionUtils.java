package wuchen.com.citypicker.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by 巫晨 on 2017/6/4.
 */

public class PermissionUtils {

    private static String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static ArrayList<String> mStrings;

    static {
        if(mStrings == null) {
            mStrings = new ArrayList<>();
        }
    }

    public static String[] getPermissions(Context context) {
        mStrings.clear();
        for (int i = 0; i < permissions.length; i++) {
            int permission = ContextCompat.checkSelfPermission(context, permissions[i]);
            if(permission == PackageManager.PERMISSION_DENIED) {
                //没有获得
                mStrings.add(permissions[i]);
            }
        }
        return mStrings.toArray(new String[mStrings.size()]);
    }

}
