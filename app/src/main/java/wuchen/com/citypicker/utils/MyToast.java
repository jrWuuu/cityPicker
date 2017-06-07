package wuchen.com.citypicker.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 巫晨 on 2017/6/3.
 */

public class MyToast {

    private MyToast(){};

    private static Toast sToast;

    public static void show(Context context, String str) {
        if(sToast == null) {
            sToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        sToast.setText(str);
        sToast.show();
    }

}
