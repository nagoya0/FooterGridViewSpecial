/**
 * 
 */
package com.misocast.footergridview.sample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.misocast.widget.GridItem;

public class UserData implements GridItem {
    private Context mContext;

    public UserData(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap getBitmapInBackground(int pos) {
        Resources r = mContext.getResources();
        return BitmapFactory.decodeResource(r, R.drawable.ic_launcher_android);
    }

}
