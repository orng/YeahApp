package com.missingstatement.yeahapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.missingstatement.yeahapp.networking.SearchHandler;
import com.missingstatement.yeahapp.networking.SearchTask;
import com.missingstatement.yeahapp.utils.Keys;
import com.missingstatement.yeahapp.utils.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class GetIncomingCallReceiver extends BroadcastReceiver
{
    private final String TAG = getClass().getSimpleName();
    private final int NO_RESULT = 0;

    private Context mContext;

    private CallSearchHandler mCallSearchHandler;
    private Toast mCallerInfoToast;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        mContext = context;

        View callerInfoView = View.inflate(context, R.layout.caller_info, null);
        mCallSearchHandler = new CallSearchHandler(callerInfoView);

        Log.e(TAG, "Receiving a call!");

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IncomingCallPhoneStateListener incomingCallPhoneStateListener = new IncomingCallPhoneStateListener();
        telephonyManager.listen(incomingCallPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class CallSearchHandler implements SearchHandler
    {
        private View view;

        public CallSearchHandler(View view)
        {
            this.view = view;
        }

        public void handleSearchResponse(ArrayList<HashMap<String, ArrayList<String>>> results)
        {
            Log.e(TAG, "handling response...");
            Log.e(TAG, "results: " + results.size());

            if(results.size() ==  NO_RESULT)
            {
                return;
            }
            HashMap<String, ArrayList<String>> result = results.get(0);

            ArrayList<String> names = result.get(Keys.KEY_NAMES);
            ArrayList<String> addresses = result.get(Keys.KEY_ADDRESSES);
            ArrayList<String> phoneNumbers = result.get(Keys.KEY_PHONE_NUMBERS);

            Log.e(TAG, "names: " + names);
            Log.e(TAG, "addresses: " + addresses);
            Log.e(TAG, "phoneNumbers: " + phoneNumbers);

            ((TextView) view.findViewById(R.id.txt_name)).setText(names.get(0));
            ((TextView) view.findViewById(R.id.txt_address)).setText(addresses.get(0));
            ((TextView) view.findViewById(R.id.txt_phoneNumber)).setText(phoneNumbers.get(0));

            (view.findViewById(R.id.layout_searching)).setVisibility(View.GONE);
            (view.findViewById(R.id.layout_caller_info)).setVisibility(View.VISIBLE);

            mCallerInfoToast.cancel();

            Toast test = new Toast(mContext);
            test.setView(view);
            test.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            test.setDuration(Toast.LENGTH_LONG);
            test.show();
        }
    }

    private class IncomingCallPhoneStateListener extends PhoneStateListener
    {
        private final String TAG = getClass().getSimpleName();
        
        public boolean numberInContacts(String number)
        {
        	Uri lookupUri = Uri.withAppendedPath(
        	PhoneLookup.CONTENT_FILTER_URI, 
        	Uri.encode(number));
        	String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
        	Cursor cur = mContext.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        	try 
        	{
        	   if (cur.moveToFirst()) 
        	   {
        	      return true;
        	   }
        	} 
        	finally 
        	{
        		if (cur != null)
        			cur.close();
        	}
        	return false;
        }
        

        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            switch(state)
            {
                case TelephonyManager.CALL_STATE_RINGING:

                    if( !Utils.isNetworkOn(mContext) )
                    {
                        return;
                    }
                    
                    if(numberInContacts(incomingNumber))
                    {
                    	return;
                    }

                    Log.e(TAG, "Incoming number: " + incomingNumber);

                    View view = View.inflate(mContext, R.layout.caller_info, null);

                    (view.findViewById(R.id.layout_searching)).setVisibility(View.VISIBLE);
                    String searchText = mContext.getString(R.string.label_toast_searching);

                    searchText = MessageFormat.format(searchText, incomingNumber);

                    ((TextView) view.findViewById(R.id.label_searching)).setText(searchText);

                    boolean isNextTask = false;
                    SearchTask searchTask = new SearchTask(mContext, mCallSearchHandler, isNextTask);
                    searchTask.execute(incomingNumber);

                    if(mCallerInfoToast == null)
                    {
                        mCallerInfoToast = new Toast(mContext);
                        mCallerInfoToast.setDuration(10000);
                        mCallerInfoToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        mCallerInfoToast.setView(view);
                        mCallerInfoToast.show();
                    }

                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    //TODO: verify that this is called when call is ended
                    Log.e(TAG, "idle");
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //TODO: find out when this happens
                    Log.e(TAG, "offHook");
            }
        }
    }

}


