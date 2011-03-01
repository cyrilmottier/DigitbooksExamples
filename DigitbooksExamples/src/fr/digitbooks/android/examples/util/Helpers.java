package fr.digitbooks.android.examples.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Helpers {

    private static final String LOG_TAG = Helpers.class.getSimpleName();

    private Helpers() {
    }

    /**
     * Retourne la disponibilité d'une connection réseau
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(LOG_TAG, "erreur, service indisponible");
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (Config.INFO_LOGS_ENABLED) {
                            Log.i(LOG_TAG, "Network is available");
                        }
                        
                        return true;
                    }
                }
            }
        }
        
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "Network is not available");
        }
        
        return false;
    }
}
