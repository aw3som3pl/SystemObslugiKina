package com.wat.jannowakowski.systemobslugikina.global;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Jan Nowakowski on 14.05.2018.
 */

public class InternetConnectionListener{

    private static Context appContext;

    private static InternetConnectionListener INSTANCE = null;

    private InternetConnectionListener(){}


    public static InternetConnectionListener getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new InternetConnectionListener();
        }
        return(INSTANCE);
    }

    public static void setContext(Context context) {
        appContext = context;
    }

    public boolean isConnectedToInternet(){
            ConnectivityManager connectivity = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }
            }
            return false;
        }
    }

