package com.fanap.podchat.util.NetworkUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class VPNChecker {


    private static final String TAG = "CHAT_SDK_VPN";


    static boolean hasVPN(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            return hasLollipopVpn(context);

        } else {

            return hasPreLollipopVpn();
        }

    }


    private static boolean hasPreLollipopVpn() {

        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp()) {
                    String netName = networkInterface.getName();
                    if (containVPNName(netName)) {
                        Log.i(TAG, "A VPN detected. name: " + netName);
                        return true;
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "ERROR detecting VPN: " + e.getMessage());
            Log.e(TAG, "Network List didn't received");
        }

        return false;

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean hasLollipopVpn(Context mContext) {


        try {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = cm.getAllNetworks();
            for (Network network : networks) {

                NetworkCapabilities caps = cm.getNetworkCapabilities(network);

                if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    Log.i(TAG, "Network " + network + " has transport VPN");
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR detecting VPN. cause: " + e.getMessage());
        }


        return false;
    }


    private static boolean containVPNName(String netName) {
        return netName.contains("tun") ||
                netName.contains("ppp") ||
                netName.contains("pptp");
    }

}
