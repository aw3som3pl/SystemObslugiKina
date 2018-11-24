package com.wat.jannowakowski.systemobslugikina.serviceListeners;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Jan Nowakowski on 14.05.2018.
 */

public class InternetConnectionListener {

        private final Context _context;

        public InternetConnectionListener(Context context){
            this._context = context;
        }

        /**
         * Checking for all possible internet providers
         * **/
        public boolean isConnectingToInternet(){
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

