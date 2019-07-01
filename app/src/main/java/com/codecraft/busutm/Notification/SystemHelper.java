package com.codecraft.busutm.Notification;

import android.content.Context;
import android.util.TypedValue;
import android.widget.Toast;

public class SystemHelper {

    private Context context;

    public  SystemHelper(Context context){
        this.context=context;
    }

    public void toast(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public int dpToPx(int dpValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
