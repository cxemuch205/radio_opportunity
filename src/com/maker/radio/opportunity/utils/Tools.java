package com.maker.radio.opportunity.utils;

import com.maker.radio.opportunity.R;
import com.maker.radio.opportunity.constant.App;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

public class Tools {
	
	public static Toast toast;
	private static String lastToastText;
	
	public static boolean checkInternetConnection(Context ctx) {
	    try {
	        ConnectivityManager nInfo = (ConnectivityManager) ctx.getSystemService(
	            Context.CONNECTIVITY_SERVICE);
	        nInfo.getActiveNetworkInfo().isConnectedOrConnecting();
	        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getActiveNetworkInfo();
	        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	            return true;
	        } else {
	            return false;
	        }
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	public static void showToast(Context context, String text) {		

		if (toast != null) {
	       //  toast.cancel();
	      }
		
		if (text != null && text.equals(lastToastText)) {
	         toast.show();
	      } else {
	         lastToastText = text;
	         LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	         TextView tvTextToast = (TextView) inflater.inflate(R.layout.custom_toast_notif, null);
	         tvTextToast.setText(text);
		
	         toast = Toast.makeText(context, text, Toast.LENGTH_LONG);		
	         toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	         toast.setView(tvTextToast);
	         toast.show();
	    }		
	}
	
	public static SharedPreferences getPreferences(Context ctx){
		return ctx.getSharedPreferences(App.PREF_APP, 0);
	}

}
