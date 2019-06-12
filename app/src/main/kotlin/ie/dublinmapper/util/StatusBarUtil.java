package ie.dublinmapper.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

public class StatusBarUtil {

    public static void setLightStatusBar(View view, Activity activity){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

}
