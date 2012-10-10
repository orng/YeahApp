package com.missingstatement.yeahapp.networking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.missingstatement.yeahapp.JaSearcher;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 9/23/12
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchTask extends AsyncTask<String, Void, ArrayList<HashMap<String, ArrayList<String>>>> {

    private SearchHandler mSearchHandler;
    private JaSearcher mJaSearcher;

    private Context mContext;
    private ProgressDialog mProgressDialog;

    public SearchTask(Context context, SearchHandler handler) {
        this(context, new JaSearcher(), handler);
    }

    public SearchTask(Context context, JaSearcher jaSearcher, SearchHandler handler)  {
        mContext = context;
        mSearchHandler = handler;
        mJaSearcher = jaSearcher;
    }

    public void initProgressDialog(String title, String message)
    {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute()
    {
        if( mProgressDialog !=null && !mProgressDialog.isShowing() )
        {
            mProgressDialog.show();
        }
    }
    @Override
    protected ArrayList<HashMap<String, ArrayList<String>>> doInBackground(String... urls)
    {

        return mJaSearcher.search(urls[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, ArrayList<String>>> result)
    {
        mSearchHandler.handleSearchResponse(result);

        dismissDialog();
    }
    @Override
    public void onCancelled()
    {
        dismissDialog();
    }

    private void dismissDialog() {
        if(mProgressDialog!=null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
    }
}
