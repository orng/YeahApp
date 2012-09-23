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

import java.text.MessageFormat;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        Log.e(TAG, "Receiving a call!");
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IncomingCallPhoneStateListener incomingCallPhoneStateListener = new IncomingCallPhoneStateListener();
        telephonyManager.listen(incomingCallPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
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

                    ((TextView) view.findViewById(R.id.txt_name)).setText("name");

                    ((TextView) view.findViewById(R.id.txt_address)).setText("Address");
                    ((TextView) view.findViewById(R.id.txt_phoneNumber)).setText(incomingNumber);



                    Toast toast = new Toast(mContext);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(view);
                    toast.show();


                    toast.cancel();




                    break;
            }
        }
    }

}


