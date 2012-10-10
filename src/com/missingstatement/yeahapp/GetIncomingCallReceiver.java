package com.missingstatement.yeahapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.missingstatement.yeahapp.networking.SearchHandler;
import com.missingstatement.yeahapp.networking.SearchTask;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 9/23/12
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetIncomingCallReceiver extends BroadcastReceiver{

    private final String TAG = getClass().getSimpleName();

    private Context mContext;
    private SearchTask mSearchTask;
    private CallSearchHandler mCallSearchHandler;
    private Toast mCallerInfoToast;


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        if(mSearchTask == null) {
            View view = View.inflate(context, R.layout.caller_info, null);

            mCallSearchHandler = new CallSearchHandler(view);
            mSearchTask = new SearchTask(mContext, mCallSearchHandler);
        }
        Log.e(TAG, "Receiving a call!");
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IncomingCallPhoneStateListener incomingCallPhoneStateListener = new IncomingCallPhoneStateListener();
        telephonyManager.listen(incomingCallPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private class CallSearchHandler implements SearchHandler {

        private View view;
        public CallSearchHandler(View view) {
            this.view = view;
        }

        @Override
        public void handleSearchResponse(ArrayList<HashMap<String, ArrayList<String>>> results) {


            Log.e(TAG, "handling response...");
            HashMap<String, ArrayList<String>> result = results.get(0);

            ArrayList<String> names = result.get("Names");
            ArrayList<String> addresses = result.get("Address");
            ArrayList<String> phoneNumbers = result.get("PhoneNrs");

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

    private class IncomingCallPhoneStateListener extends PhoneStateListener {

        private final String TAG = getClass().getSimpleName();

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch(state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "Incoming number: " + incomingNumber);

                    View view = View.inflate(mContext, R.layout.caller_info, null);

                    (view.findViewById(R.id.layout_searching)).setVisibility(View.VISIBLE);
                    String searchText = mContext.getString(R.string.label_searching);

                    searchText = MessageFormat.format(searchText, incomingNumber);

                    ((TextView) view.findViewById(R.id.label_searching)).setText(searchText);

                    mSearchTask.execute(incomingNumber);


                    if(mCallerInfoToast == null) {
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


