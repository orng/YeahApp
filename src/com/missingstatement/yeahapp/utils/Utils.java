package com.missingstatement.yeahapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 10/10/12
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils
{

    public static boolean isNetworkOn(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}
